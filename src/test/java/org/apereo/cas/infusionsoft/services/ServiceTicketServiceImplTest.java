package org.apereo.cas.infusionsoft.services;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Test cases for the {@link ServiceTicketServiceImpl} class
 */
public class ServiceTicketServiceImplTest {

    private ServiceTicketService classToTest;

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    @Mock
    Logger logger;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        classToTest = new ServiceTicketServiceImpl();
        Whitebox.setInternalState(classToTest, "entityManager", entityManager);
    }

    @Test
    public void testDeleteOrphanedServiceTicketRecords() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(3);
        int deletedCount = classToTest.deleteOrphanedServiceTicketRecords();
        verify(query).executeUpdate();
        Assert.assertEquals(deletedCount, 3);
    }
}
