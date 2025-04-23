/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.bugs;

import org.apache.logging.log4j.LogManager;
import org.hibernate.bugs.hhh19381.*;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.testing.orm.junit.*;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using its built-in unit test framework.
 * Although ORMStandaloneTestCase is perfectly acceptable as a reproducer, usage of this class is much preferred. Since
 * we nearly always include a regression test with bug fixes, providing your reproducer using this method simplifies the
 * process.
 * <p>
 * What's even better?  Fork hibernate-orm itself, add your test case directly to a module's unit tests, then submit it
 * as a PR!
 */
@DomainModel(
        annotatedClasses = {
                Child.class,
                GrandParent.class,
                Parent.class,
                H2Other.class,
                H2Some.class,
                H2OtherHistory.class
        },
        // If you use *.hbm.xml mappings, instead of annotations, add the mappings here.
        xmlMappings = {
                // "org/hibernate/test/Foo.hbm.xml",
                // "org/hibernate/test/Bar.hbm.xml"
        }
)
@ServiceRegistry(
        // Add in any settings that are specific to your test.  See resources/hibernate.properties for the defaults.
        settings = {
                // For your own convenience to see generated queries:
                @Setting(name = AvailableSettings.SHOW_SQL, value = "true"),
                @Setting(name = AvailableSettings.FORMAT_SQL, value = "true"),
                // @Setting( name = AvailableSettings.GENERATE_STATISTICS, value = "true" ),

                // Add your own settings that are a part of your quarkus configuration:
                // @Setting( name = AvailableSettings.SOME_CONFIGURATION_PROPERTY, value = "SOME_VALUE" ),
        }
)
@SessionFactory
class ORMUnitTestCase {

    private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(ORMUnitTestCase.class);

    // Add your tests, using standard JUnit 5.
//    @Test
    @RepeatedTest(10)
    void hhh123Test(SessionFactoryScope scope) throws Exception {
        scope.inTransaction(session -> {

            final var grandParent = new GrandParent();
            session.persist(grandParent);
            logger.info("grandParent: " + grandParent);

//            final var hasGrandGrandParent = false;
            final var hasGrandGrandParent = true;
            if (hasGrandGrandParent) {
                final var grandGrandParent = new GrandParent();
                session.persist(grandGrandParent);
                grandParent.setGrandGrandParent(grandGrandParent);
                session.merge(grandParent);
            }
            logger.info("grandParent: " + grandParent);

            final var parent = new Parent();
            parent.setGrandParent(grandParent);
            session.persist(parent);
            logger.info("parent: " + parent);

            final var child = new Child();
            child.setParent(parent);
            session.persist(child);
            logger.info("child: " + child);

            final var list = session.createQuery("SELECT e FROM Child AS e WHERE e.parent = :parent", Child.class)
                    .setParameter("parent", parent)
                    .list();
            assert list.contains(child);
        });
    }

//    @Test
    @RepeatedTest(10)
    void hhh123Test2(SessionFactoryScope scope) throws Exception {
        scope.inTransaction(session -> {
            session.clear();

            // ---------------------------------------------------------------------------------------------------------
            final var some = new H2Some();
            session.persist(some);

            // https://github.com/spring-projects/spring-data-jpa/issues/3850
            // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            final var parent = (new H2Some());
            session.persist(parent);
//            parent.setParent(null);
            some.setParent(parent);
            session.merge(some);
            // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

            // ---------------------------------------------------------------------------------------------------------
            final var other = new H2Other();
            other.setSome(some);
            session.persist(other);
            // ---------------------------------------------------------------------------------------------------------
            final var otherHistory = new H2OtherHistory();
            otherHistory.setOther(other);
            session.persist(otherHistory);
            // ---------------------------------------------------------------------------------------------------------
            final var found = session.createQuery(
                    " SELECT e FROM H2OtherHistory AS e WHERE e.other = :other"
            ).setParameter("other", other).getResultList();
            assert found.contains(otherHistory);
        });
    }
}
