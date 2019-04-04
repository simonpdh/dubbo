/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.dubbo.demo.consumer;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.demo.DemoService;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Application {
    private static final ReentrantLock LOCK = new ReentrantLock();

    private static final Condition STOP = LOCK.newCondition();
    /**
     * In order to make sure multicast registry works, need to specify '-Djava.net.preferIPv4Stack=true' before
     * launch the application
     */
    public static void main(String[] args) throws Exception {
        try {
            ReferenceConfig<DemoService> reference = new ReferenceConfig<>();
            reference.setApplication(new ApplicationConfig("dubbo-demo-api-consumer"));
            reference.setRegistry(new RegistryConfig("zookeeper://10.0.161.2:2181"));
            reference.setInterface(DemoService.class);
            reference.setTimeout(6000);
            reference.setRetries(5);
            for(int i=0;i<1;i++){
                DemoService service = reference.get();
                String message = service.sayHello("provider"+i);
            }

            LOCK.lock();
            STOP.await();
        } catch (InterruptedException e) {
            System.out.println("consumer error"+e);
        } finally {
            LOCK.unlock();
        }
    }
}
