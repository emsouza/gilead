package net.sf.gilead.core;

import java.sql.Timestamp;
import java.util.Date;

import junit.framework.TestCase;
import net.sf.gilead.test.DAOFactory;
import net.sf.gilead.test.dao.IMessageDAO;
import net.sf.gilead.test.domain.interfaces.IMessage;

public class TimestampTest extends TestCase {
    // -------------------------------------------------------------------------
    //
    // Test methods
    //
    // -------------------------------------------------------------------------
    /**
     * Test timestamp conversion
     */
    public void testTimestampConversion() {

        // Hibernate lazy manager
        //
        PersistentBeanManager beanManager = TestHelper.initStatelessBeanManager();

        // Initialize DB if needed
        //
        if (TestHelper.isInitialized() == false) {
            TestHelper.initializeDB();
        }

        // Test data
        //
        IMessageDAO messageDAO = DAOFactory.getMessageDAO();
        IMessage message = messageDAO.loadLastMessage();
        assertNotNull(message);
        message = messageDAO.loadDetailedMessage(message.getId());
        assertNotNull(message);

        Timestamp date = new Timestamp(new Date().getTime());
        date.setNanos(200);
        message.setDate(date);

        // Clone
        //
        IMessage cloneMessage = (IMessage) beanManager.clone(message);

        // Clone verification
        //
        assertNotNull(cloneMessage);
        assertNotNull(cloneMessage.getDate());
        assertTrue(cloneMessage.getDate() instanceof Date);
        assertTrue(cloneMessage.getDate() instanceof java.sql.Timestamp);
        assertEquals(message.getDate(), cloneMessage.getDate());

        assertEquals(message.getMessage(), cloneMessage.getMessage());
        assertEquals(message.getId(), cloneMessage.getId());

        // Author must have been cloned, not copied
        assertNotNull(cloneMessage.getAuthor());
        assertEquals(message.getAuthor().getId(), cloneMessage.getAuthor().getId());
        assertFalse(message.getAuthor() == cloneMessage.getAuthor());

        // Merge
        //
        IMessage mergedMessage = (IMessage) beanManager.merge(cloneMessage);

        // Merge verification
        //
        assertNotNull(mergedMessage);
        assertNotNull(mergedMessage.getDate());
        assertTrue(mergedMessage.getDate() instanceof java.sql.Timestamp);
        assertEquals(message.getDate(), mergedMessage.getDate());

        assertEquals(message.getMessage(), mergedMessage.getMessage());
        assertEquals(message.getId(), mergedMessage.getId());

        // Author must have been merged, not copied
        assertNotNull(mergedMessage.getAuthor());
        assertEquals(message.getAuthor().getId(), mergedMessage.getAuthor().getId());
        assertFalse(message.getAuthor() == mergedMessage.getAuthor());
    }
}