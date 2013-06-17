package com.infusionsoft.cas.services;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * User: joe.koberstein
 * Date: 6/17/13 * Time: 10:41 AM
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Logger.class})
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
        PowerMockito.mockStatic(Logger.class);
        Mockito.when(Logger.getLogger(ServiceTicketServiceImpl.class)).thenReturn(logger);
        classToTest = new ServiceTicketServiceImpl();
        Whitebox.setInternalState(classToTest, "entityManager", entityManager);
    }

    @Test
    public void testDeleteOrphanedServiceTicketRecords(){
        Mockito.when(entityManager.createNativeQuery(Mockito.anyString())).thenReturn(query);
        Mockito.when(query.executeUpdate()).thenReturn(3);
        classToTest.deleteOrphanedServiceTicketRecords();
        Mockito.verify(query).executeUpdate();
        Mockito.verify(logger).info("ServiceTicketService deleted 3 orphaned service tickets");
    }
}
