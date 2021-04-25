package fr.inria.gforge.spoon.transformation.dbaccess.processing;

import fr.inria.gforge.spoon.transformation.dbaccess.annotation.DBAccess;
import fr.inria.gforge.spoon.transformation.dbaccess.annotation.DBType;
import fr.inria.gforge.spoon.transformation.dbaccess.template.DBCodeTemplate;
import spoon.processing.AbstractAnnotationProcessor;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.support.Level;
import spoon.template.Substitution;

public class DBAccessProcessor extends
        AbstractAnnotationProcessor<DBAccess, CtClass<?>> {

    @Override
	public void process(final DBAccess dbAccess, final CtClass<?> target) {
        final Factory f = target.getFactory();
        final DBType t = dbAccess.type();
        if (t != DBType.RELATIONAL) {
			f.getEnvironment().report(
					this,
					Level.ERROR,
					target.getAnnotation(f.Type().createReference(
							DBAccess.class)), "unsupported DB system");
		}

        final DBCodeTemplate template = new DBCodeTemplate(f, dbAccess.database(),
                dbAccess.username(), dbAccess.password(), dbAccess.tableName());
        Substitution.insertField(target, template, f.Class().get(
                DBCodeTemplate.class).getField("connection"));
        for (final CtConstructor<?> c : target.getConstructors()) {
            c.getBody().insertBegin((CtStatement)
                    Substitution.substituteMethodBody(target, template,
                            "initializerCode"));
        }
        for (final CtMethod<?> m : target.getMethods()) {
            template._columnName_ = m.getSimpleName().substring(3)
                    .toLowerCase();
            m.getBody().replace(
                    Substitution.substituteMethodBody(target, template,
                            "accessCode"));
        }
    }

}
