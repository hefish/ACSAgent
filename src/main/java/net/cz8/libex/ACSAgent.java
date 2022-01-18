package net.cz8.libex;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.logging.Logger;

public class ACSAgent {
    private static final Logger logger = Logger.getLogger(ACSAgent.class.getName());

    public static void premain(String agentArgs, Instrumentation inst) {
        logger.info("ACSAgent: agent working...");
        inst.addTransformer(new ACSTransformer(), true);
    }

    static class ACSTransformer implements ClassFileTransformer {

        private static final Logger logger = Logger.getLogger(ACSTransformer.class.getName());

        @Override
        public byte[] transform(ClassLoader loader, String className, Class <?> classBeingRedefined,
                                ProtectionDomain protectionDomain, byte[] classfileBuffer ) throws IllegalClassFormatException {

            final ClassPool classPool = ClassPool.getDefault();
            if ("com/interlib/b/b".equals(className)) {
                logger.info("Agent: load class -> " + className);
                CtClass loggerType = null;
                try {
                    loggerType = classPool.get("org.apache.log4j.Logger");

                } catch(NotFoundException e) {
                    logger.info("not found org.apache.log4j.Logger class");
                }
                try {

                    final CtClass clazz = classPool.get("com.interlib.b.b");
                    CtMethod newMethod = clazz.getDeclaredMethod("a", new CtClass[] { loggerType });
                    String methodBody = "{ return true;  } ";
                    newMethod.setBody(methodBody);

                    byte[] byteCode = clazz.toBytecode();
                    clazz.detach();

                    return byteCode;
                } catch( Exception e) {
                    e.printStackTrace();
                }

            }
            return classfileBuffer;
        }
    }



}
