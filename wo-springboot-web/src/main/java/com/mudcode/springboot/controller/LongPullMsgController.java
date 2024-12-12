package com.mudcode.springboot.controller;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/message")
public class LongPullMsgController {

    private Multimap<String, DeferredResult<ResponseEntity<?>>> watchRequests;

    @PostConstruct
    public void init() {
        this.watchRequests = Multimaps.synchronizedMultimap(HashMultimap.create());
    }

    @PreDestroy
    public void destroy() {
        this.watchRequests
                .forEach((id, deferredResult) -> deferredResult.setResult(ResponseEntity.status(HttpStatus.GONE).build()));
    }

    @GetMapping(value = "/get")
    public DeferredResult<ResponseEntity<?>> subscribeMessage(@RequestParam(name = "id") String id) {
        long timeoutValue = TimeUnit.SECONDS.toMillis(60);
        ResponseEntity<?> timeoutResult = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>(timeoutValue, timeoutResult);
        deferredResult.onCompletion(() -> watchRequests.remove(id, deferredResult));
        watchRequests.put(id, deferredResult);
        return deferredResult;
    }

    @PostMapping(value = "/put")
    public ResponseEntity<?> publishMessage(@RequestParam(name = "id") String id,
                                            @RequestParam(name = "msg") String msg) {
        if (this.watchRequests.containsKey(id)) {
            this.watchRequests.get(id).forEach(deferredResult -> deferredResult.setResult(ResponseEntity.ok(msg)));
        }
        return ResponseEntity.ok().build();
    }

}
