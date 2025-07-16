package com.kasino.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kasino.dto.SpinRequestDto;
import com.kasino.dto.SpinResultDto;
import com.kasino.service.SlotMachineService;

@RestController
@RequestMapping("/api/slot")
public class SlotMachineController {

    @Autowired
    private SlotMachineService slotMachineService;

    @PostMapping("/play")
    public ResponseEntity<SpinResultDto> play(@RequestBody SpinRequestDto request, Principal principal) {
        String username = principal.getName(); // získanie mena aktuálne prihláseného používateľa
        SpinResultDto result = slotMachineService.spin(username, request);
        return ResponseEntity.ok(result);
    }
}

