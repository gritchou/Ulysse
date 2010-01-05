package org.qualipso.factory.test.sessionbean;

import java.util.ArrayList;

import javax.ejb.SessionContext;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.Topic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.junit.BeforeClass;
import org.junit.Test;
import org.qualipso.factory.indexing.IndexingService;
import org.qualipso.factory.indexing.IndexingServiceBean;
import org.qualipso.factory.indexing.SearchResult;
import org.qualipso.factory.indexing.base.IndexBase;

import com.bm.testsuite.BaseSessionBeanFixture;

public class IndexingServiceTest extends BaseSessionBeanFixture<IndexingServiceBean> {

    private static Log logger = LogFactory.getLog(IndexingServiceTest.class);

    @SuppressWarnings("unchecked")
    private static final Class[] usedBeans = {};

    public IndexingServiceTest() {
        super(IndexingServiceBean.class, usedBeans);
    }

    private Mockery mockery;
    private SessionContext ctx;
    private Topic topic;
    private ConnectionFactory connectionFactory;
    private IndexBase index;

    @BeforeClass
    public void setUp() throws Exception {
        super.setUp();
        logger.debug("injecting mock partners session beans");
        mockery = new Mockery();
        ctx = mockery.mock(SessionContext.class);
        topic = mockery.mock(Topic.class);
        connectionFactory = mockery.mock(ConnectionFactory.class);
        index = mockery.mock(IndexBase.class);

        getBeanToTest().setSessionContext(ctx);
        getBeanToTest().setTopic(topic);
        getBeanToTest().setConnectionFactory(connectionFactory);
        getBeanToTest().setIndex(index);

    }

    @Test
    public void testCRUDIndexing() {
        logger.debug("testing CRUDIndex..");
        final Sequence sequence1 = mockery.sequence("sequence1");
        final Sequence sequence2 = mockery.sequence("sequence2");
        final Sequence sequence3 = mockery.sequence("sequence3");
        final Sequence sequence4 = mockery.sequence("sequence4");

        try {
            mockery.checking(new Expectations() {
                {
                    oneOf(connectionFactory).createConnection().createSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE).createProducer(topic).send(with(any(Message.class)));
                    inSequence(sequence1);
                }
            });
            IndexingService service = getBeanToTest();
            service.index(new String());
            mockery.assertIsSatisfied();

            mockery.checking(new Expectations() {
                {
                	oneOf(connectionFactory).createConnection().createSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE).createProducer(topic).send(with(any(Message.class)));
                    inSequence(sequence2);
                }
            });
            service.reindex(new String());
            mockery.assertIsSatisfied();

            mockery.checking(new Expectations() {
                {
                	oneOf(connectionFactory).createConnection().createSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE).createProducer(topic).send(with(any(Message.class)));
                    inSequence(sequence3);
                }
            });
            service.remove(new String());
            mockery.assertIsSatisfied();

            mockery.checking(new Expectations() {
                {
                    allowing(index).search("bug AND forge");
                    will(returnValue(new ArrayList<SearchResult>()));
                    inSequence(sequence4);
                }
            });
            service.search("bug AND forge");
            mockery.assertIsSatisfied();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }

    }
}
