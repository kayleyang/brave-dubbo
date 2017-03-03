package com.github.kristofa.brave.dubbo;

import com.github.kristofa.brave.*;
import com.github.kristofa.brave.http.HttpSpanCollector;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.FactoryBean;

/**
 * Created by jack-cooper on 2017/2/20.
 */
public class BraveFactoryBean implements FactoryBean<Brave> {
    /**服务名*/
    private String serviceName;
    /**zipkin服务器ip及端口，不配置默认打印日志*/
    private String zipkinHost;
    /**单例模式*/
    private Brave instance;

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getZipkinHost() {
        return zipkinHost;
    }

    public void setZipkinHost(String zipkinHost) {
        this.zipkinHost = zipkinHost;
    }

    private void createInstance() {
        if (this.serviceName == null) {
            throw new BeanInitializationException("Property serviceName must be set.");
        }
        Brave.Builder builder = new Brave.Builder(this.serviceName);
        if (this.zipkinHost != null && !"".equals(this.zipkinHost)) {
            builder.spanCollector(HttpSpanCollector.create(this.zipkinHost, new EmptySpanCollectorMetricsHandler()));
        }else{
            builder.spanCollector(new LoggingSpanCollector()).traceSampler(Sampler.create(1.0f)).build();
        }
        this.instance = builder.build();
    }



    @Override
    public Brave getObject() throws Exception {
        if (this.instance == null) {
            this.createInstance();
        }
        return this.instance;
    }

    @Override
    public Class<?> getObjectType() {
        return Brave.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}