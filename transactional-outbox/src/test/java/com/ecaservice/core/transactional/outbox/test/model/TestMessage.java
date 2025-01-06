package com.ecaservice.core.transactional.outbox.test.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestMessage {

    private int x;

    private int y;
}
