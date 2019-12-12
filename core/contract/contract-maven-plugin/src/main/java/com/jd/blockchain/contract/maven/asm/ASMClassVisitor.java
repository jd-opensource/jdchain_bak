package com.jd.blockchain.contract.maven.asm;

import com.jd.blockchain.contract.maven.ContractClass;
import com.jd.blockchain.contract.maven.ContractMethod;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ASMClassVisitor extends ClassVisitor {

    private ContractClass contractClass;

    public ASMClassVisitor(ContractClass contractClass) {
        super(Opcodes.ASM5);
        this.contractClass = contractClass;
    }
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor superMV = super.visitMethod(access, name, desc, signature, exceptions);
        ContractMethod method = this.contractClass.method(name);
        return new ASMMethodVisitor(superMV, method);
    }
}