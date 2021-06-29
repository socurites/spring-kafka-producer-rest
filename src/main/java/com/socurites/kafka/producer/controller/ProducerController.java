package com.socurites.kafka.producer.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaSendCallback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.socurites.kafka.producer.vo.UserEvent;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProducerController {
	private static final Logger log = LoggerFactory.getLogger(ProducerController.class);

	private final KafkaTemplate<String, String> kafkaTemplate;
	
	private final String TOPIC_NAME = "select-color";
	
	private final static Gson gson = new Gson();
	
	public ProducerController(KafkaTemplate<String, String> kafkaTemplate) {
		super();
		this.kafkaTemplate = kafkaTemplate;
	}
	
	@GetMapping("/api/select")
	public void selectColor(@RequestHeader("user-agent") String userAgent,
			@RequestParam(value = "color") String colorName,
			@RequestParam(value = "user") String userName) {
		String userEventJson = buildMessage(userAgent, colorName, userName);
		
		ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC_NAME, userEventJson);
		future.addCallback(new KafkaSendCallback<String, String>() {

			@Override
			public void onSuccess(SendResult<String, String> result) {
				log.info(result.toString());
			}

			@Override
			public void onFailure(Throwable ex) {
				log.error(ex.getMessage(), ex);
			}

			@Override
			public void onFailure(KafkaProducerException ex) {
				log.error(ex.getMessage(), ex);
			}
		});
	}
	
	private String buildMessage(String userAgent, String colorName, String userName) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
		UserEvent userEvent = new UserEvent(sdf.format(new Date()), userAgent, colorName, userName);
		return gson.toJson(userEvent);
	}
}
