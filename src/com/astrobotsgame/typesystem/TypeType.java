package com.astrobotsgame.typesystem;

import com.astrobotsgame.syntax.Type;

import java.util.List;
import java.util.Map;

public class TypeType {

    public static boolean isNumber(Type type) {
        return type.accept(new Type.Visitor<Boolean>() {
            @Override
            public Boolean number() {
                return true;
            }

            @Override
            public Boolean variable(String name) {
                return false;
            }

            @Override
            public Boolean sumType(String name, List<Type> typeParameters) {
                return false;
            }

            @Override
            public Boolean recordType(String name, List<Type> typeParameters) {
                return false;
            }

            @Override
            public Boolean functionType(Map<String, Type> parameterTypes, Type returnType) {
                return false;
            }
        });
    }

    public static String isVariable(Type type) {
        return type.accept(new Type.Visitor<String>() {
            @Override
            public String number() {
                return null;
            }

            @Override
            public String variable(String name) {
                return name;
            }

            @Override
            public String sumType(String name, List<Type> typeParameters) {
                return null;
            }

            @Override
            public String recordType(String name, List<Type> typeParameters) {
                return null;
            }

            @Override
            public String functionType(Map<String, Type> parameterTypes, Type returnType) {
                return null;
            }
        });
    }
}
