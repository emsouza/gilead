package net.sf.gilead.core;

import junit.framework.TestCase;
import net.sf.gilead.test.HibernateContext;
import net.sf.gilead.test.domain.misc.Page;
import net.sf.gilead.test.domain.misc.Photo;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Test case for checking collection order handling
 * 
 * @author bruno.marchesson
 */
public class CollectionOrderTest extends TestCase {
    // -------------------------------------------------------------------------
    //
    // Test methods
    //
    // -------------------------------------------------------------------------
    /**
     * Test change order
     */
    public void testChangeOrder() {
        PersistentBeanManager beanManager = TestHelper.initStatelessBeanManager();

        // Create test page
        //
        Page testPage = createTestPage("Test page");
        testPage = loadPage(testPage.getName());

        // Clone test page
        //
        Page clonePage = (Page) beanManager.clone(testPage);

        // clone page checking
        assertNotNull(clonePage);
        assertNotNull(clonePage.getPhotoList());

        // Change order (clea
        //
        Photo photo1 = clonePage.getPhotoList().get(0);
        Photo photo2 = clonePage.getPhotoList().get(1);
        Photo photo3 = clonePage.getPhotoList().get(2);
        clonePage.getPhotoList().clear();
        clonePage.addPhoto(photo3);
        clonePage.addPhoto(photo2);
        clonePage.addPhoto(photo1);

        // Merge test page
        //
        Page mergePage = (Page) beanManager.merge(clonePage);

        // merge page checking
        assertNotNull(mergePage);
        assertNotNull(mergePage.getPhotoList());
        assertEquals(photo3.getUrl(), mergePage.getPhotoList().get(0).getUrl());
        assertEquals(photo2.getUrl(), mergePage.getPhotoList().get(1).getUrl());
        assertEquals(photo1.getUrl(), mergePage.getPhotoList().get(2).getUrl());

        // Test ordering after save
        //
        savePage(mergePage);
        Page loadPage = loadPage(mergePage.getName());

        assertNotNull(loadPage);
        assertNotNull(loadPage.getPhotoList());
        assertEquals(3, loadPage.getPhotoList().size());
        assertEquals(photo3.getUrl(), loadPage.getPhotoList().get(0).getUrl());
        assertEquals(photo2.getUrl(), loadPage.getPhotoList().get(1).getUrl());
        assertEquals(photo1.getUrl(), loadPage.getPhotoList().get(2).getUrl());

    }

    /**
     * Other change order algorithm on client side
     */
    public void testChangeOrder2() {
        PersistentBeanManager beanManager = TestHelper.initStatelessBeanManager();

        // Create test page
        //
        Page testPage = createTestPage("Test page 2");
        testPage = loadPage(testPage.getName());

        // Clone test page
        //
        Page clonePage = (Page) beanManager.clone(testPage);

        // clone page checking
        assertNotNull(clonePage);
        assertNotNull(clonePage.getPhotoList());

        // Change order
        //
        Photo photo1 = clonePage.getPhotoList().get(0);
        Photo photo2 = clonePage.getPhotoList().get(1);
        Photo photo3 = clonePage.getPhotoList().get(2);
        clonePage.getPhotoList().set(0, photo3);
        clonePage.getPhotoList().set(1, photo2);
        clonePage.getPhotoList().set(2, photo1);

        // Merge test page
        //
        Page mergePage = (Page) beanManager.merge(clonePage);

        // merge page checking
        assertNotNull(mergePage);
        assertNotNull(mergePage.getPhotoList());
        assertEquals(photo3.getUrl(), mergePage.getPhotoList().get(0).getUrl());
        assertEquals(photo2.getUrl(), mergePage.getPhotoList().get(1).getUrl());
        assertEquals(photo1.getUrl(), mergePage.getPhotoList().get(2).getUrl());

        // Test ordering after save
        //
        savePage(mergePage);
        assertNotNull(mergePage);
        Page loadPage = loadPage(mergePage.getName());

        assertNotNull(loadPage);
        assertNotNull(loadPage.getPhotoList());
        assertEquals(3, loadPage.getPhotoList().size());
        assertEquals(photo3.getUrl(), loadPage.getPhotoList().get(0).getUrl());
        assertEquals(photo2.getUrl(), loadPage.getPhotoList().get(1).getUrl());
        assertEquals(photo1.getUrl(), loadPage.getPhotoList().get(2).getUrl());

    }

    // -------------------------------------------------------------------------
    //
    // Internal methods
    //
    // -------------------------------------------------------------------------
    /**
     * Create a test page
     */
    private Page createTestPage(String name) {
        // Page
        Page page = new Page();
        page.setName(name);

        // Photos
        Photo photo1 = new Photo();
        photo1.setUrl("Photo1");
        page.addPhoto(photo1);

        Photo photo2 = new Photo();
        photo2.setUrl("Photo2");
        page.addPhoto(photo2);

        Photo photo3 = new Photo();
        photo3.setUrl("Photo3");
        page.addPhoto(photo3);

        // Save page
        savePage(page);

        return page;
    }

    /**
     * Save Page
     */
    private void savePage(Page page) {
        Session session = null;
        Transaction transaction = null;
        try {
            // Get session
            //
            session = HibernateContext.getSessionFactory().getCurrentSession();
            transaction = session.beginTransaction();

            // Save user
            //
            session.saveOrUpdate(page);
            transaction.commit();

        } catch (RuntimeException e) {
            // Rollback
            //
            transaction.rollback();
            throw e;
        }
    }

    /**
     * Load the page with the argument name
     */
    public Page loadPage(String pageName) {
        Session session = null;
        Transaction transaction = null;
        try {
            // Get session
            //
            session = HibernateContext.getSessionFactory().getCurrentSession();
            transaction = session.beginTransaction();

            // Create query
            //
            StringBuffer hqlQuery = new StringBuffer();
            hqlQuery.append("select distinct page");
            hqlQuery.append(" from Page page");
            hqlQuery.append(" where page.name=:name");

            // Fill query
            //
            Query query = session.createQuery(hqlQuery.toString());
            query.setString("name", pageName);

            // Execute query
            //
            Page page = (Page) query.uniqueResult();
            page.getPhotoList().size();
            session.evict(page);
            transaction.commit();

            return page;
        } catch (RuntimeException e) {
            // Rollback
            //
            transaction.rollback();
            throw e;
        }
    }
}
