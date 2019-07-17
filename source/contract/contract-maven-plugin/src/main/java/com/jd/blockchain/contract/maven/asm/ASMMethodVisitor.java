package com.jd.blockchain.contract.maven.asm;

import com.jd.blockchain.contract.maven.ContractMethod;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;

import java.util.ArrayList;
import java.util.List;

public class ASMMethodVisitor extends MethodVisitor {

    private ContractMethod method;

    public ASMMethodVisitor(MethodVisitor mv, ContractMethod method) {
        super(Opcodes.ASM5, mv);
        this.method = method;
    }

    @Override
    public void visitFieldInsn(int type, String cName, String fName, String fType) {
        if (type == 178 || type == 179) {
            this.method.addStaticField(cName, fName, fType);
        } else {
            this.method.addField(cName, fName, fType);
        }
        super.visitFieldInsn(type, cName, fName, fType);
    }

    @Override
    public void visitMethodInsn(int type, String cName, String mName, String params, boolean b) {
        ParamsAndReturn paramsAndReturn = resolveParamsAndReturn(params);
        this.method.addMethod(cName, mName, paramsAndReturn.paramTypes, paramsAndReturn.returnTypes);
        super.visitMethodInsn(type, cName, mName, params, b);
    }

    private ParamsAndReturn resolveParamsAndReturn(String params) {
        // 格式：
        // 1、(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        // 2、()I
        // 3、(Ljava/lang/String;)V
        // 4、()V
        // 5、([Ljava/lang/Object;)Ljava/util/List; false
        // 从上面分析可以得出：括号内的是入参，右括号后面的是返回值，其中V表示Void，即空；
        String[] paramArray = params.split("\\)");
        String paramTypeChars = "";
        if (!paramArray[0].equals("(")) {
            // 表明入参不为空
            paramTypeChars = paramArray[0].split("\\(")[1];
        }
        String returnTypeChars = paramArray[1];
        return new ParamsAndReturn(paramTypeChars, returnTypeChars);
    }

    static class ParamsAndReturn {

        String[] paramTypes;

        String[] returnTypes;

        public ParamsAndReturn(String paramsTypeChars, String returnTypeChars) {
            initParamsType(paramsTypeChars);
            initReturnType(returnTypeChars);
        }

        private void initParamsType(String paramsTypeChars) {
            List<String> paramList = handleTypes(paramsTypeChars);
            if (!paramList.isEmpty()) {
                this.paramTypes = new String[paramList.size()];
                paramList.toArray(this.paramTypes);
            }
        }

        private void initReturnType(String returnTypeChar) {
            // 按照分号分隔
            List<String> returnList = handleTypes(returnTypeChar);
            if (!returnList.isEmpty()) {
                this.returnTypes = new String[returnList.size()];
                returnList.toArray(this.returnTypes);
            }
        }

        private List<String> handleTypes(String typeChars) {
            String[] types = typeChars.split(";");
            List<String> typeList = new ArrayList<>();
            if (types.length > 0) {
                for (String type : types) {
                    if (type.length() > 0) {
                        if (type.startsWith("[L") && type.length() > 2) {
                            // 说明是数组
                            typeList.add(type.substring(2) + "[]");
                        } else if (type.startsWith("[") && type.length() > 1) {
                            // 说明是数组
                            typeList.add(type.substring(1));
                        } else if (type.startsWith("L") && type.length() > 1) {
                            // 说明是非基础类型
                            typeList.add(type.substring(1));
                        } else {
                            typeList.add(type);
                        }
                    }
                }
            }
            return typeList;
        }
    }
}