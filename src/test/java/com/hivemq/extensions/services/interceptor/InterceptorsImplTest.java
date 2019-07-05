package com.hivemq.extensions.services.interceptor;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.annotations.Nullable;
import com.hivemq.extension.sdk.api.interceptor.connect.ConnectInterceptor;
import com.hivemq.extension.sdk.api.interceptor.connect.ConnectInterceptorProvider;
import com.hivemq.extension.sdk.api.interceptor.connect.parameter.ConnectInterceptorProviderInput;
import com.hivemq.extensions.HiveMQExtension;
import com.hivemq.extensions.HiveMQExtensions;
import com.hivemq.extensions.classloader.IsolatedPluginClassloader;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.net.URL;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Lukas Brandl
 */
public class InterceptorsImplTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private InterceptorsImpl interceptors;

    @Mock
    private HiveMQExtensions hiveMQExtensions;

    @Mock
    private HiveMQExtension plugin1;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        interceptors = new InterceptorsImpl(hiveMQExtensions);

        when(hiveMQExtensions.getExtension("plugin1")).thenReturn(plugin1);
    }


    @Test
    public void test_add_and_remove() throws Exception {

        final JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class)
                .addClass("com.hivemq.extensions.services.interceptor.InterceptorsImplTest$TestConnectInterceptorProvider");

        final File jarFile = temporaryFolder.newFile();
        javaArchive.as(ZipExporter.class).exportTo(jarFile, true);

        //This classloader contains the classes from the jar file
        final IsolatedPluginClassloader cl = new IsolatedPluginClassloader(new URL[]{jarFile.toURI().toURL()}, this.getClass().getClassLoader());

        final Class<?> classOne = cl.loadClass("com.hivemq.extensions.services.interceptor.InterceptorsImplTest$TestConnectInterceptorProvider");

        final ConnectInterceptorProvider connectInterceptorProvider = (ConnectInterceptorProvider) classOne.newInstance();

        when(hiveMQExtensions.getExtensionForClassloader(any(IsolatedPluginClassloader.class))).thenReturn(plugin1);
        when(plugin1.getId()).thenReturn("plugin1");

        final Channel channelMock = Mockito.mock(Channel.class);
        final ChannelPipeline pipelineMock = Mockito.mock(ChannelPipeline.class);

        when(channelMock.pipeline()).thenReturn(pipelineMock);

        interceptors.addConnectInterceptorProvider(connectInterceptorProvider);

        final ArgumentCaptor<Consumer> captor = ArgumentCaptor.forClass(Consumer.class);
        verify(hiveMQExtensions).addAfterExtensionStopCallback(captor.capture());

        assertSame(connectInterceptorProvider, interceptors.connectInterceptorProviders().get("plugin1"));

        when(plugin1.getPluginClassloader()).thenReturn(cl);
        captor.getValue().accept(plugin1);
        assertEquals(0, interceptors.connectInterceptorProviders().size());

    }

    @Test
    public void test_plugin_null() throws Exception {

        final JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class)
                .addClass("com.hivemq.extensions.services.interceptor.InterceptorsImplTest$TestConnectInterceptorProvider");

        final File jarFile = temporaryFolder.newFile();
        javaArchive.as(ZipExporter.class).exportTo(jarFile, true);

        //This classloader contains the classes from the jar file
        final IsolatedPluginClassloader cl = new IsolatedPluginClassloader(new URL[]{jarFile.toURI().toURL()}, this.getClass().getClassLoader());

        final Class<?> classOne = cl.loadClass("com.hivemq.extensions.services.interceptor.InterceptorsImplTest$TestConnectInterceptorProvider");

        final ConnectInterceptorProvider connectInterceptorProvider = (ConnectInterceptorProvider) classOne.newInstance();

        when(hiveMQExtensions.getExtensionForClassloader(any(IsolatedPluginClassloader.class))).thenReturn(null);
        when(plugin1.getId()).thenReturn("plugin1");

        final Channel channelMock = Mockito.mock(Channel.class);
        final ChannelPipeline pipelineMock = Mockito.mock(ChannelPipeline.class);

        when(channelMock.pipeline()).thenReturn(pipelineMock);

        interceptors.addConnectInterceptorProvider(connectInterceptorProvider);

        assertEquals(0, interceptors.connectInterceptorProviders().size());

    }

    public static class TestConnectInterceptorProvider implements ConnectInterceptorProvider {
        @Override
        public @Nullable ConnectInterceptor getConnectInterceptor(@NotNull ConnectInterceptorProviderInput input) {
            return (connectInterceptorInput, connectInterceptorOutput) -> {

            };
        }
    }
}