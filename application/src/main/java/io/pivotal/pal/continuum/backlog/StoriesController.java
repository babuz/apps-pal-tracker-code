package io.pivotal.pal.continuum.backlog;

import io.pivotal.pal.continuum.backlog.data.StoryRecord;
import io.pivotal.pal.continuum.backlog.data.StoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.StreamSupport;

import static io.pivotal.pal.continuum.backlog.StoryInfo.storyInfoBuilder;
import static io.pivotal.pal.continuum.backlog.data.StoryRecord.storyRecordBuilder;
import static java.util.stream.Collectors.toList;


@RestController
@RequestMapping("/stories")
public class StoriesController {

    private final StoryRepository repository;

    public StoriesController(StoryRepository repository) {
        this.repository = repository;
    }


    @GetMapping
    public ResponseEntity<StoryInfoList> list() {
        List<StoryInfo> timeEntries = StreamSupport
            .stream(repository.findAll().spliterator(), false)
            .map(this::present)
            .collect(toList());

        return new ResponseEntity<>(new StoryInfoList(timeEntries), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoryInfo> show(@PathVariable long id) {
        StoryRecord record = repository.findOne(id);

        if (record != null) {
            return new ResponseEntity<>(present(record), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<StoryInfo> create(@RequestBody StoryForm form) {
        StoryRecord record = repository.save(storyRecordBuilder()
            .projectId(form.projectId)
            .name(form.name)
            .build()
        );
        return new ResponseEntity<>(present(record), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoryInfo> update(@PathVariable long id, @RequestBody StoryForm form) {
        StoryRecord record = repository.save(storyRecordBuilder()
            .id(id)
            .projectId(form.projectId)
            .name(form.name)
            .build()
        );
        return new ResponseEntity<>(present(record), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable long id) {
        repository.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }


    private StoryInfo present(StoryRecord record) {
        return storyInfoBuilder()
            .id(record.id)
            .projectId(record.projectId)
            .name(record.name)
            .build();
    }
}
