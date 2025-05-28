package com.notes.mapper.front.notes;

import com.notes.domain.front.notes.ESNotes;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ESNotesRepository extends ElasticsearchRepository<ESNotes, String> {
}
