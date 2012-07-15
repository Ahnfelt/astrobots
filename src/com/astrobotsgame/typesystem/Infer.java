package com.astrobotsgame.typesystem;

import com.astrobotsgame.syntax.Function;
import com.astrobotsgame.syntax.Type;

import java.util.HashMap;
import java.util.Map;

public class Infer {

    private static Type.Factory types = Type.factory;

    public static Type type(Function function) {
        HashMap<String, Type> environment = new HashMap<String, Type>();
        for (String parameter: function.parameters) {
            environment.put(parameter, types.uniqueVariable());
        }
        TypeChecker typeChecker = new TypeChecker(environment);
        Type returnType = function.body.accept(typeChecker);
        Map<String, Type> newEnvironment = typeChecker.getEnvironment();
        Map<String, Type> parameterTypes = new HashMap<String, Type>();
        for (String parameter: function.parameters) {
            parameterTypes.put(parameter, newEnvironment.get(parameter));
        }
        return types.functionType(parameterTypes , returnType);
    }

}
