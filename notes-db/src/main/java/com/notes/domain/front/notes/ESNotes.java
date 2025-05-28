package com.notes.domain.front.notes;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.List;

@Document(indexName = "notes")
@Data
public class ESNotes {

    @Id
    private Long id;

    @Field(type = FieldType.Keyword)
    private Long notesUserId;

    @MultiField(mainField=@Field(type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_smart"),
        otherFields = { @InnerField(suffix = "keyword", type = FieldType.Keyword) })
    private String tag;

    @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_smart"),
        otherFields = { @InnerField(suffix = "keyword", type = FieldType.Keyword) })
    private String title;

    @MultiField(mainField=@Field(type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_smart"),
        otherFields = { @InnerField(suffix = "keyword", type = FieldType.Keyword) })
    private String source;

    @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_smart"),
        otherFields = { @InnerField(suffix = "keyword", type = FieldType.Keyword) })
    private String content;

    // 添加语义向量字段
    @Field(type = FieldType.Dense_Vector, dims = 512)  // 注意：dims 要根据你的模型维度调整，例如768维
    private List<Double> embedding;
}
