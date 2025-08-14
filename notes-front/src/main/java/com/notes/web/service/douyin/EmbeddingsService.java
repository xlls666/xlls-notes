package com.notes.web.service.douyin;

//@Service
public class EmbeddingsService {
    /*private final String model ;
    private final ArkService service ;

    public EmbeddingsService() {
        model = KvConfigUtils.getConfigValue(KvConfigConstants.DY_MODEL);
        service = ArkService.builder()
            .dispatcher(new Dispatcher())
            .connectionPool(new ConnectionPool(5, 1, TimeUnit.SECONDS))
            .apiKey(KvConfigUtils.getConfigValue(KvConfigConstants.DY_MODEL_API_KEY))
            .build();
    }

    private final static int dim = 2048;


    public List<List<Double>> getEmbeddingList(List<String> textList) {
        EmbeddingRequest embeddingRequest = EmbeddingRequest.builder()
            .model(model)
            .input(textList)
            .build();

        EmbeddingResult res = service.createEmbeddings(embeddingRequest);
        *//*return res.getData().stream().map(embedding -> {
            List<Double> vector = embedding.getEmbedding();
            if (vector == null || vector.size() < dim) {
                throw new IllegalArgumentException("输入向量长度不足 " + dim);
            }

            List<Double> sliced = vector.subList(0, dim);
            double norm = 0.0;
            for (Double val : sliced) {
                norm += val * val;
            }
            norm = Math.sqrt(norm);

            List<Double> normalized = new ArrayList<>(dim);
            for (Double val : sliced) {
                normalized.add(val / norm);
            }
            return normalized;
        }).collect(Collectors.toList());*//*
        return res.getData().stream().map(Embedding::getEmbedding).collect(Collectors.toList());
    }*/
}
