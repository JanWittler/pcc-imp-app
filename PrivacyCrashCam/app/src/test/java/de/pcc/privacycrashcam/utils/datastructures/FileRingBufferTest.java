package de.pcc.privacycrashcam.utils.datastructures;

import android.os.FileObserver;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Queue;

import static org.junit.Assert.assertTrue;

/**
 * Created by Josh Romanowski on 29.01.2017.
 */
public class FileRingBufferTest {

    @Mock
    private FileObserver observer;
    private FileRingBuffer buffer;

    @Before
    public void setUp() {
        buffer = new FileRingBuffer(2);
    }

    @Test
    public void insert() {

    }

    @Ignore
    @Test
    public void demandDataTest() throws InterruptedException {

        observer = Mockito.mock(FileObserver.class);

        Mockito.doAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) {
                System.out.println("got message");
                return "";
            }
        }).when(observer).onEvent(Mockito.anyInt(), Mockito.anyString());

        final ClassLoader classLoader = getClass().getClassLoader();

        for (int i = 0; i < 2; i++) {

            final int finalI = i;
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(finalI + ".txt");
                    URL resource = classLoader.getResource(finalI + ".txt");
                    File file = new File(resource.getPath());
                    buffer.put(file);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    observer.onEvent(1, "");
                }
            });
            t.start();
        }

        Thread.sleep(100);
        Queue<File> data = buffer.demandData();
        System.out.println(Arrays.toString(data.toArray()));
        assertTrue(!data.isEmpty());
    }
}