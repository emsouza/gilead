package net.sf.gilead.annotations;

import junit.framework.TestCase;
import net.sf.gilead.core.annotations.AnnotationsManager;
import net.sf.gilead.test.domain.annotated.Message;
import net.sf.gilead.test.domain.annotated.User;
import net.sf.gilead.test.domain.misc.Configuration;

/**
 * Test case for annotation manager.
 * 
 * @author bruno.marchesson
 */
public class AnnotationsManagerTest extends TestCase {

    /**
     * Test method for
     * {@link net.sf.gilead.core.annotations.AnnotationsManager#hasServerOnlyAnnotations(java.lang.Class)}.
     */
    public final void testHasServerOnlyAnnotations() {
        // Annotation on property
        assertTrue(AnnotationsManager.hasServerOnlyAnnotations(Message.class));

        // Annotation on getter
        assertTrue(AnnotationsManager.hasServerOnlyAnnotations(User.class));

        // No ServerOnly annotation
        assertFalse(AnnotationsManager.hasServerOnlyAnnotations(Configuration.class));
    }

    /**
     * Test method for
     * {@link net.sf.gilead.core.annotations.AnnotationsManager#isServerOnly(java.lang.Class, java.lang.String)}.
     */
    public final void testIsServerOnly() {
        // Annotation on property
        Message message = new Message();
        assertTrue(AnnotationsManager.isServerOnly(message, "version"));
        assertFalse(AnnotationsManager.isServerOnly(message, "author"));
        assertFalse(AnnotationsManager.isServerOnly(message, "doesNotExist"));

        // Annotation on getter
        User user = new User();
        assertTrue(AnnotationsManager.isServerOnly(user, "password"));
        assertFalse(AnnotationsManager.isServerOnly(user, "login"));
        assertFalse(AnnotationsManager.isServerOnly(user, "doesNotExist"));
    }

    /**
     * Test method for {@link net.sf.gilead.core.annotations.AnnotationsManager#hasReadOnlyAnnotations(java.lang.Class)}
     * .
     */
    public final void testHasReadOnlyAnnotations() {
        // Annotation on property
        assertTrue(AnnotationsManager.hasReadOnlyAnnotations(Message.class));

        // Annotation on getter
        assertTrue(AnnotationsManager.hasReadOnlyAnnotations(User.class));

        // No ServerOnly annotation
        assertFalse(AnnotationsManager.hasReadOnlyAnnotations(Configuration.class));
    }

    /**
     * Test method for
     * {@link net.sf.gilead.core.annotations.AnnotationsManager#isReadOnly(java.lang.Class, java.lang.String)}.
     */
    public final void testIsReadOnly() {
        // Annotation on property
        Message message = new Message();
        assertFalse(AnnotationsManager.isReadOnly(message, "comment"));
        assertFalse(AnnotationsManager.isReadOnly(message, "message"));
        assertFalse(AnnotationsManager.isReadOnly(message, "doesNotExist"));

        // Annotation on getter
        User user = new User();
        assertTrue(AnnotationsManager.isReadOnly(user, "login"));
        assertFalse(AnnotationsManager.isReadOnly(user, "password"));
        assertFalse(AnnotationsManager.isReadOnly(user, "doesNotExist"));

        // Test on private getter
        // assertTrue(AnnotationsHelper.isReadOnly(Address.class, "zipCode"));
    }

    /**
     * Test access manager handling
     */
    public final void testAccessManager() {
        TestAccessManager accessManager = new TestAccessManager();
        AnnotationsManager.setAccessManager(accessManager);

        // Test access with role user
        accessManager.setRole(TestAccessManager.Role.user);
        Message message = new Message();
        assertTrue(AnnotationsManager.isReadOnly(message, "comment"));

        // Test with admin user
        accessManager.setRole(TestAccessManager.Role.admin);
        assertFalse(AnnotationsManager.isReadOnly(message, "comment"));
    }
}
