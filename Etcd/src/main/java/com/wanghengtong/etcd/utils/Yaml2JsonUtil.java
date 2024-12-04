package com.wanghengtong.etcd.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Yaml2JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * yaml字符串转json字符串
     *
     * @param yamlString
     * @return
     */
    public static List<String> yamlConverToJson(String yamlString) {
        Yaml yaml = new Yaml();
        //yaml中可以通过 --- 实现同一个yaml中配置多个资源，loadAll会根据 --- 进行拆分，生成多个对象，所以是List
        Iterable<Object> object = yaml.loadAll(yamlString);
        List<String> yamlList = new ArrayList<>();
        object.forEach(o -> {
            if (Objects.nonNull(o)) {
                try {
                    yamlList.add(objectMapper.writeValueAsString(o));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        });
        return yamlList;
    }

    /**
     * json字符串转yaml字符串
     *
     * @param jsonString
     * @return
     */
    public static String jsonConverToYaml(String jsonString) {
        Yaml yaml = new Yaml();
        Map<String, Object> map = yaml.load(jsonString);
        return yaml.dumpAsMap(map);
    }

    public static void main(String[] args) throws JsonProcessingException {
        String yamlString = "apiVersion: apps/v1\nkind: Deployment\nmetadata:\n  name: nginx-deployment\nspec:\n  selector:\n    " + "matchLabels:\n      app: nginx\n  replicas: 2 # tells deployment to run 2 pods matching the template\n  template:\n    " + "metadata:\n      labels:\n        app: nginx\n    spec:\n      containers:\n      - name: nginx\n        " + "image: nginx:1.14.2\n        ports:\n        - containerPort: 80\n---\napiVersion: v1\nkind: Service\nmetadata:\n  " + "name: my-service\nspec:\n  selector:\n    app: nginx\n  ports:\n    - protocol: TCP\n      port: 80\n      targetPort: 80";
        List<String> yamlList = Yaml2JsonUtil.yamlConverToJson(yamlString);
        for (String yaml : yamlList) {
            System.out.println("yaml转json：" + yaml);
        }

        for (String yaml : yamlList) {
            String jsonConverToYaml = Yaml2JsonUtil.jsonConverToYaml(yaml);
            System.out.println("json转yaml：\n" + jsonConverToYaml);
        }
    }

}
