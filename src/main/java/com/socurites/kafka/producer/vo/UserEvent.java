package com.socurites.kafka.producer.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserEvent {
	private final String timestamp;
	private final String userAgent;
	private final String colorName;
	private final String userName;
}
