package ru.practicum.ewm.main.stat.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.stats.dto.dto.CreateStatsDto;
import ru.practicum.ewm.stats.dto.dto.StatsDto;


import java.util.List;

@FeignClient(value = "stats-client", url = "${statsclient.url}")
public interface StatsClient {

    @PostMapping("/hit")
    CreateStatsDto createStats(@RequestBody CreateStatsDto creationDto);

    @GetMapping("/stats?start={start}&end={end}&uris={uris}&unique={unique}")
    List<StatsDto> getStats(@RequestParam String start,
                            @RequestParam String end,
                            @RequestParam(required = false) String[] uris,
                            @RequestParam(defaultValue = "false") boolean unique);
}
