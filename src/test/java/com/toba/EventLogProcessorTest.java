package com.toba;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class EventLogProcessorTest {

    private static final ObjectReader OBJECT_READER = new ObjectMapper().readerFor(ApplicationEvent.class);

    EventLogProcessor eventLogProcessor;

    DatabaseApi databaseApi;



    @Before
    public void setUp() throws Exception {
        this.databaseApi = mock(DatabaseApi.class);
        eventLogProcessor = new EventLogProcessor(new FileBasedDataSource(new File(getClass().getClassLoader().getResource("testInput.txt").getFile()).getAbsolutePath()), databaseApi);
    }

    @Test
    public void run() throws Exception{

        File file = ResourceUtils.getFile("classpath:testInput.txt");

        LineIterator iterator = FileUtils.lineIterator(file);

        ArrayList<ApplicationEvent> events = new ArrayList<>();

        while (iterator.hasNext()){
            events.add(OBJECT_READER.readValue(iterator.nextLine()));
        }

        eventLogProcessor.run();

        for(ApplicationEvent event : events){
            verify(databaseApi, times(1)).insertOrUpdate(event);
        }
    }
}