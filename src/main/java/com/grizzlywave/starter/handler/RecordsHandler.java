package com.grizzlywave.starter.handler;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grizzlywave.starter.configuration.context.AppContext;
import com.grizzlywave.starter.configuration.postprocessor.BeanMethodPair;
import com.grizzlywave.starter.service.IOEventService;

import lombok.extern.slf4j.Slf4j;

/** Records handler to invoke method when consuming records from topic */
@Slf4j
@Service
public class RecordsHandler {

	private String recordTarget;

	@Autowired
	private AppContext ctx;
	@Autowired
	private IOEventService ioEventService;

	/** method which call doInvoke Method **/
	public void process(ConsumerRecords<String, String> consumerRecords, Object bean, Method method) throws Throwable {
		for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {

			this.doInvoke(method, bean, consumerRecord.value());
		}

	}

	/** method to invoke the method from a specific bean **/
	public void doInvoke(Method method, Object bean, Object args) throws Throwable {
		Object beanmObject = ctx.getApplicationContext().getBean(bean.getClass());
		if (beanmObject != null) {

			for (Method met : beanmObject.getClass().getDeclaredMethods()) {
				if (met.getName().equals(method.getName())) {
					met.invoke(ctx.getApplicationContext().getBean(bean.getClass()), args);
				}
			}

		}
	}

	public void process(ConsumerRecords<String, String> consumerRecords, List<BeanMethodPair> beanMethodPairs)
			throws Throwable {
		for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
			consumerRecord.headers().forEach(header -> {
				if (header.key().equals("targetEvent")) {
					recordTarget = new String(header.value());
				}
				;
			});
			for (BeanMethodPair pair : beanMethodPairs) {
				for (String SourceName : ioEventService.getSourceNames(pair.getIoEvent())) {
					if (SourceName.equals(recordTarget)) {
						this.doInvoke(pair.getMethod(), pair.getBean(), consumerRecord.value());

					}
				}
			}

		}
	}
}
