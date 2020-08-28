package net.sf.gilead.core;

import java.io.FileNotFoundException;
import java.util.Collection;

import junit.framework.TestCase;
import net.sf.gilead.core.store.stateful.AbstractStatefulProxyStore;
import net.sf.gilead.core.store.stateful.InMemoryProxyStore;
import net.sf.gilead.test.HibernateContext;
import net.sf.gilead.test.domain.misc.BaseDictionary;
import net.sf.gilead.test.domain.misc.Client;
import net.sf.gilead.test.domain.misc.Page;
import net.sf.gilead.test.domain.misc.PersistentException;
import net.sf.gilead.test.domain.misc.Photo;
import net.sf.gilead.test.domain.misc.Preference;
import net.sf.gilead.test.domain.misc.Project;
import net.sf.gilead.test.domain.misc.SomeDictionary;
import net.sf.gilead.test.domain.misc.Utente;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Non regression test based on user feedback
 *
 * @author bruno.marchesson
 */
public class NonRegressionTest extends TestCase {
    // -------------------------------------------------------------------------
    //
    // Test methods
    //
    // -------------------------------------------------------------------------
    /**
     * Test order set preservation
     *
     * @throws FileNotFoundException
     */
    public void testOrderedSet() throws FileNotFoundException {
        // Init bean manager
        PersistentBeanManager beanManager = TestHelper.initJava5SupportBeanManager();

        // Create test user and reload it
        Utente utente = createTestUtente();
        utente = loadUser(utente.getId());

        // test order
        int index = 1;
        for (Preference pref : utente.getPreferences()) {
            assertEquals(index++, pref.getIntValue());
        }

        // Clone utente
        utente = (Utente) beanManager.clone(utente);

        // Test preferences order on cloned bean
        index = 1;
        for (Preference pref : utente.getPreferences()) {
            assertEquals(index++, pref.getIntValue());
        }

        // Add a new preference
        Preference newPreference = new Preference();
        newPreference.setIntValue(utente.getPreferences().size() + 1);
        newPreference.setUser(utente);
        utente.getPreferences().add(newPreference);

        // Remove first element
        utente.getPreferences().remove(utente.getPreferences().iterator().next());

        // Merge and save user
        utente = (Utente) beanManager.merge(utente);
        index = 2;
        for (Preference pref : utente.getPreferences()) {
            assertEquals(index++, pref.getIntValue());
        }
        save(utente);

        // Reload it
        Utente loaded = loadUser(utente.getId());
        assertNotNull(loaded.getPreferences());
        assertEquals(utente.getPreferences().size(), loaded.getPreferences().size());

        index = 2;
        for (Preference pref : loaded.getPreferences()) {
            assertEquals(index++, pref.getIntValue());
        }
    }

    /**
     * Clone an exception that contains persistent attribute
     */
    public void testCloneException() {
        PersistentBeanManager beanManager = TestHelper.initStatelessBeanManager();

        // Create exception
        Page page = createTestPage();
        page = loadPage(page.getName());
        PersistentException exception = new PersistentException(page);

        // Clone exception
        PersistentException cloneException = (PersistentException) beanManager.clone(exception);
        assertNotNull(cloneException);
        assertNotNull(cloneException.getPage());
    }

    /**
     * Test merge on many to many
     */
    public void testMergeManyToMany() throws FileNotFoundException {
        // Init bean manager
        PersistentBeanManager beanManager = TestHelper.initJava5SupportBeanManager();

        // Create test client
        Client client = createTestClient();

        // Clone client
        client = (Client) beanManager.clone(client);

        // Update projects
        Collection<Project> projects = client.getProjects();
        for (Project p : projects) {
            p.getClients().remove(client);
        }
        projects.clear();

        Project p1 = new Project();
        p1.getClients().add(client);
        projects.add(p1);

        // Merge client
        client = (Client) beanManager.merge(client);
        assertEquals(1, client.getProjects().size());
    }

    /**
     * Test merge on create new entity (with name) and embeddable on client side
     *
     * @throws FileNotFoundException
     */
    public void testEntityNameOnNewItem() throws FileNotFoundException {
        // Init bean manager
        PersistentBeanManager beanManager = TestHelper.initJava5SupportBeanManager();
        AbstractStatefulProxyStore proxyStore = new InMemoryProxyStore();
        proxyStore.setPersistenceUtil(beanManager.getPersistenceUtil());
        beanManager.setProxyStore(proxyStore);

        // Create dictionaries
        BaseDictionary baseDictionary = new BaseDictionary();
        baseDictionary.setName("testDictionary");
        save(baseDictionary);

        // Clone dictionary
        BaseDictionary cloneDictionary = (BaseDictionary) beanManager.clone(baseDictionary);
        assertNotNull(cloneDictionary);

        SomeDictionary dictionary = new SomeDictionary(new SomeDictionary.PrimaryKey(1, 1));
        dictionary.setBaseDictionary(cloneDictionary);

        SomeDictionary childDictionary = new SomeDictionary(new SomeDictionary.PrimaryKey(1, 2));
        childDictionary.setBaseDictionary(cloneDictionary);
        dictionary.addChild(childDictionary);

        // Merge dictionary
        BaseDictionary mergedDictionary = (BaseDictionary) beanManager.merge(cloneDictionary);
        assertNotNull(mergedDictionary);
    }

    // -------------------------------------------------------------------------
    //
    // Internal methods
    //
    // -------------------------------------------------------------------------
    /**
     * Create a test entity
     */
    private Utente createTestUtente() {
        // User
        Utente user = new Utente();

        // Preferences
        for (int index = 1; index <= 4; index++) {
            Preference preference = new Preference();
            preference.setUser(user);
            preference.setIntValue(index);
            user.getPreferences().add(preference);
        }

        // Save user and preferences
        save(user);

        return user;
    }

    /**
     * Load the user with the argument id
     */
    public Utente loadUser(Integer id) {
        Session session = null;
        Transaction transaction = null;
        try {
            // Get session
            session = HibernateContext.getSessionFactory().getCurrentSession();
            transaction = session.beginTransaction();

            Utente user = (Utente) session.get(Utente.class, id);
            user.getPreferences().size();
            transaction.commit();

            return user;
        } catch (RuntimeException e) {
            // Rollback
            transaction.rollback();
            throw e;
        }
    }

    /**
     * Create a test page
     */
    private Page createTestPage() {
        // Page
        Page page = new Page();
        page.setName("Test page");

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
        save(page);

        return page;
    }

    /**
     * Load the page with the argument name
     */
    public Page loadPage(String pageName) {
        Session session = null;
        Transaction transaction = null;
        try {
            // Get session
            session = HibernateContext.getSessionFactory().getCurrentSession();
            transaction = session.beginTransaction();

            // Create query
            StringBuffer hqlQuery = new StringBuffer();
            hqlQuery.append("select distinct page");
            hqlQuery.append(" from Page page");
            hqlQuery.append(" left outer join page.photoList");
            hqlQuery.append(" where page.name=:name");

            // Fill query
            Query query = session.createQuery(hqlQuery.toString());
            query.setString("name", pageName);

            // Execute query
            Page page = (Page) query.uniqueResult();
            page.getPhotoList().size();
            transaction.commit();

            return page;
        } catch (RuntimeException e) {
            // Rollback
            transaction.rollback();
            throw e;
        }
    }

    /**
     * Create a test entity
     */
    private Client createTestClient() {
        // Client
        Client client = new Client();

        // Projects
        Project project1 = new Project();
        project1.getClients().add(client);
        client.getProjects().add(project1);

        Project project2 = new Project();
        project2.getClients().add(client);
        client.getProjects().add(project2);

        // Save client
        save(client);

        return client;
    }

    /**
     * Save entity
     */
    private void save(Object object) {
        Session session = null;
        Transaction transaction = null;
        try {
            // Get session
            session = HibernateContext.getSessionFactory().getCurrentSession();
            transaction = session.beginTransaction();

            // Save user
            session.saveOrUpdate(object);
            transaction.commit();
        } catch (RuntimeException e) {
            // Rollback
            transaction.rollback();
            throw e;
        }
    }
}
