/*
 * Copyright © 2021 CodeOnce Software (https://www.codeonce.fr/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */




package com.ioevent.starter.configuration.postprocessor;






import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.common.KafkaFuture;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ioevent.starter.configuration.postprocessor.IOEventTopicBeanPostProcessor;
import com.ioevent.starter.configuration.properties.IOEventProperties;

class IOEventTopicBeanPostProcessorTest {
	@InjectMocks
	IOEventTopicBeanPostProcessor ioeventTopicBeanPostProcessor = new IOEventTopicBeanPostProcessor();
	@Mock
	AdminClient client;
	@Mock
	ListTopicsResult listTopicsResult;
	@Mock
	KafkaFuture<Set<String>> future;
	@Mock
	IOEventProperties iOEventProperties;
	@Mock
	CreateTopicsResult createResult;
	@Mock
	DeleteTopicsResult deleteResult;
	@BeforeEach
	public void init() {

		MockitoAnnotations.initMocks(this);
	}
	@Test
	void topicExist_returnTrue() throws InterruptedException, ExecutionException {
		when(client.listTopics()).thenReturn(listTopicsResult);
		when(listTopicsResult.names()).thenReturn(future);
		when(future.get()).thenReturn(new HashSet<>(Arrays.asList("test-Topic1", "Topic2","test-Topic3")));
		when(iOEventProperties.getPrefix()).thenReturn("test-");
		Assert.assertTrue(ioeventTopicBeanPostProcessor.topicExist("Topic1"));
	}
	@Test
	void topicExist_returnFalse() throws InterruptedException, ExecutionException {
		when(client.listTopics()).thenReturn(listTopicsResult);
		when(listTopicsResult.names()).thenReturn(future);
		when(future.get()).thenReturn(new HashSet<>(Arrays.asList("test-Topic1", "Topic2","test-Topic3")));
		when(iOEventProperties.getPrefix()).thenReturn("test-");
		Assert.assertFalse(ioeventTopicBeanPostProcessor.topicExist("newTopic"));
	}
}
