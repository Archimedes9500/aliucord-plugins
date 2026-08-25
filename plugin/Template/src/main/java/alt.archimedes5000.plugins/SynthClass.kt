package alt.archimedes5000.plugins.utils;

import org.objectweb.asm.*;
import org.objectweb.asm.Opcodes.*;

import kotlin.reflect.*;
import kotlin.reflect.jvm.jvmErasure;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

object loader: ClassLoader(){
	fun defineClass(name: String, bytes: ByteArray): Class<*>{
		return super.defineClass(name, bytes, 0, bytes.size);
	};
};
class SynthClass(
	val data: ClassData,
	val fields: Set<FieldData> = emptySet(),
	val methods: Set<MethodData> = emptySet(),
	val classes: Set<SynthClass> = emptySet()
){
	val Fields = FieldsAccessor(fields);
	val Methods = MethodsAccessor(methods);
	val Classes = ClassesAccessor(classes);

	val cw = ClassWriter(ClassWriter.COMPUTE_FRAMES or ClassWriter.COMPUTE_MAXS);
	val bytes: ByteArray by lazy{
		cw.visit(
			V1_7,
			(data.flags?: ACC_PUBLIC),
			data.internalName,
			data.signature,
			data.extends.internalName,
			data.implements.map{it.internalName}.toTypedArray()
		);

		/*
		public void <init>(){
			super();
			...this.field = ...
			return;
		};
		*/
		cw.visitMethod(
			ACC_PUBLIC,
			"<init>",
			"()V",
			null,
			null
		).apply{
			visitCode();
			visitVarInsn(ALOAD, 0);
			visitMethodInsn(
				INVOKESPECIAL,
				"java/lang/Object",
				"<init>",
				"()V",
				false
			);
			for(f in fields){
				visitVarInsn(ALOAD, 0);
				visitLdcInsn(f.value);
				visitFieldInsn(
					PUTFIELD,
					data.internalName,
					f.name,
					f.type.identifier
				);
			};
			visitInsn(RETURN);
			visitMaxs(0, 0);
			visitEnd();
		};

		for(f in fields){
			cw.visitField(
				f.flags?: ACC_PUBLIC,
				f.name,
				f.type.identifier,
				f.signature,
				f.value
			).apply{
				visitEnd();
			};
		};

		for(m in methods){
			cw.visitMethod(
				m.flags?: ACC_PUBLIC,
				m.name,
				m.descriptor,
				m.signature,
				m.exceptions?.map{it.internalName}?.toTypedArray()
			).also{
				if(m.body != null){
					it.visitCode();
					m.body.invoke(this, it);
					it.visitMaxs(0, 0);
				};
				it.visitEnd();
			};
		};

		cw.visitEnd();
		return@lazy cw.toByteArray();
	};
	val value: Class<*> = loader.defineClass(data.name, bytes);
	fun new() = value.getConstructor().newInstance();
	inline operator fun <reified T>get(name:  String): T{
		return if(T::class.isFun){
			val argTypes = typeOf<T>().arguments.dropLast(1).map{
				it.type!!.jvmErasure.java;
			}.toTypedArray();
			value.getDeclaredMethod(name, *argTypes).apply{isAccessible = true} as T;
		}else{
			value.getDeclaredField(name).apply{isAccessible = true}[value] as T;
		};
	};
};

@Suppress("UNCHECKED_CAST", "DEPRECATION")
fun MethodVisitor.visit(opcode: Int, vararg args: Any?){
	return when(opcode){
		NOP, ACONST_NULL, ICONST_M1, ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5, LCONST_0, LCONST_1, FCONST_0, FCONST_1, FCONST_2, DCONST_0, DCONST_1, IALOAD, LALOAD, FALOAD, DALOAD, AALOAD, BALOAD, CALOAD, SALOAD, IASTORE, LASTORE, FASTORE, DASTORE, AASTORE, BASTORE, CASTORE, SASTORE, POP, POP2, DUP, DUP_X1, DUP_X2, DUP2, DUP2_X1, DUP2_X2, SWAP, IADD, LADD, FADD, DADD, ISUB, LSUB, FSUB, DSUB, IMUL, LMUL, FMUL, DMUL, IDIV, LDIV, FDIV, DDIV, IREM, LREM, FREM, DREM, INEG, LNEG, FNEG, DNEG, ISHL, LSHL, ISHR, LSHR, IUSHR, LUSHR, IAND, LAND, IOR, LOR, IXOR, LXOR, I2L, I2F, I2D, L2I, L2F, L2D, F2I, F2L, F2D, D2I, D2L, D2F, I2B, I2C, I2S, LCMP, FCMPL, FCMPG, DCMPL, DCMPG, IRETURN, LRETURN, FRETURN, DRETURN, ARETURN, RETURN, ARRAYLENGTH, ATHROW, MONITORENTER, MONITOREXIT -> {
			visitInsn(opcode);
		};
		BIPUSH, SIPUSH, NEWARRAY -> {
			visitIntInsn(opcode, args[0] as Int);
		};
		ILOAD, LLOAD, FLOAD, DLOAD, ALOAD, ISTORE, LSTORE, FSTORE, DSTORE, ASTORE, RET -> {
			visitVarInsn(opcode, args[0] as Int);
		};
		NEW, ANEWARRAY, CHECKCAST, INSTANCEOF -> {
			visitTypeInsn(opcode, args[0] as String);
		};
		GETSTATIC, PUTSTATIC, GETFIELD, PUTFIELD -> {
			visitFieldInsn(opcode, args[0] as String, args[1] as String, args[2] as String);
		};
		INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC, INVOKEINTERFACE -> {
			visitMethodInsn(opcode, args[0] as String, args[1] as String, args[2] as String/*, args[3] as Boolean*/);
		};
		IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE, IF_ACMPEQ, IF_ACMPNE, GOTO, JSR, IFNULL, IFNONNULL -> {
			visitJumpInsn(opcode, args[0] as Label);
		};
		LDC -> {
			visitLdcInsn(args[0]);
		};
		IINC -> {
			visitIincInsn(args[0] as Int, args[1] as Int);
		};
		TABLESWITCH -> {
			visitTableSwitchInsn(args[0] as Int, args[1] as Int, args[2] as Label, *args.drop(3).map{it as Label}.toTypedArray());
		};
		LOOKUPSWITCH -> {
			visitLookupSwitchInsn(args[0] as Label, args[1] as IntArray, args[2] as Array<Label>);
		};
		MULTIANEWARRAY -> {
			visitMultiANewArrayInsn(args[0] as String, args[1] as Int);
		};
		else -> {
			error("Unsupported opcode: $opcode");
		};
	};
};
fun MethodVisitor.call(opcode: Int, vararg args: Any?): MethodVisitor{
	this.visit(opcode, *args);
	return this:
};

open class JVMEntity(
	open val name: String
){
	val internalName = name.replace('.', '/');
	val identifier = when(name.removePrefix("[")){
		"B", "C", "D", "F", "I", "J", "S", "Z" -> name;
		else -> "${if(name.indexOf("[") == 0) "[" else ""}L${internalName.removePrefix("[")};";
	};
};
open class ClassRef(
	name: String,
	val generics: Set<ClassRef> = emptySet()
): JVMEntity(name){
	val refSignature: String = if(!generics.isEmpty()){
			("${identifier.removeSuffix(";")}<"
				+generics.joinToString(""){it.refSignature}
				+">;"
			);
		}else{
			identifier;
		}
	;
};

val KType.ref: ClassRef get() = ClassRef(
	jvmErasure.java.name,
	arguments.map{it.type!!.ref}.toSet()
);
inline fun <reified T>refOf() = typeOf<T>().ref;
class MethodType(
	val argTypes: List<ClassRef> = emptyList(),
	val returnType: ClassRef
){
	constructor(type: KType): this(
		type.arguments.dropLast(1).map{it.type!!.ref},
		type.arguments.last().type!!.ref
	);
	companion object{};
};
inline operator fun <reified T>MethodType.Companion.invoke(): MethodType = MethodType(
	typeOf<T>().arguments.dropLast(1).map{it.type!!.ref},
	typeOf<T>().arguments.last().type!!.ref
);

class FieldData(
	name: String,
	val type: ClassRef,
	val value: Any?,
	val flags: Int? = null
): JVMEntity(name){
	val signature = type.refSignature;
};
class MethodData(
	name: String,
	val type: MethodType,
	val body: (SynthClass.(MethodVisitor) -> Unit)?,
	val flags: Int? = null,
	val exceptions: Set<ClassRef>? = null
): JVMEntity(name){
	val descriptor: String = (
		"("
		+type.argTypes.joinToString(""){it.identifier}
		+")"
		+type.returnType.identifier
	);
	val signature: String? = if((type.argTypes+type.returnType).any{!it.generics.isEmpty()}){
		("("
			+type.argTypes.joinToString(""){it.refSignature}
			+")"
			+type.returnType.refSignature
		);
	}else{
		null;
	};
};
class TypeParamData(
	val name: String,
	val extends: ClassRef? = null,
	val implements: Set<ClassRef> = emptySet()
){
	val signature: String = ("$name:"
		+extends?.refSignature?: extends?.identifier?: ""
		+":"
		+implements.joinToString(":"){it.refSignature}
	);
};
open class ClassData(
	name: String = object{}::class.java.name,
	val extends: ClassRef = ClassRef("java.lang.Object"),
	val implements: Set<ClassRef> = emptySet(),
	val typeParams: Set<TypeParamData> = emptySet(),
	val flags: Int? = null
): ClassRef(name){
	val signature: String? = if((implements+extends+this).any{!it.generics.isEmpty()}){
			("<"
				+typeParams.joinToString(""){it.signature}
				+">"
				+extends.refSignature
				+implements.joinToString(""){it.refSignature}
			);
		}else{
			null;
		}
	;
};

class FieldsAccessor(val fields: Set<FieldData>){
	operator fun get(name: String): FieldData{
		return fields.single{it.name == name};
	};
};
class MethodsAccessor(val methods: Set<MethodData>){
	operator fun get(name: String, type: MethodType): MethodData{
		return methods.single{it.name == name && it.type == type};
	};
};
class ClassesAccessor(val classes: Set<SynthClass>){
	operator fun get(name: String): SynthClass{
		return classes.single{it.data.name == name};
	};
};

fun runtimeCallback(
	before: (SynthClass.(MethodVisitor) -> Unit)? = null,
	after: (SynthClass.(MethodVisitor) -> Unit)? = null,
): XC_MethodHook{
	val synthClass = SynthClass(
		data = ClassData(
			name = object{}::class.java.name,
			extends = refOf<XC_MethodHook>()
		),
		methods = setOf(
			MethodData(
				name = "beforeHookedMethod",
				type = MethodType<(MethodHookParam) -> Unit>(),
				body = before?: {}
			),
			MethodData(
				name = "afterHookedMethod",
				type = MethodType<(MethodHookParam) -> Unit>(),
				body = after?: {}
			)
		)
	);
	return synthClass.new() as XC_MethodHook;
};
