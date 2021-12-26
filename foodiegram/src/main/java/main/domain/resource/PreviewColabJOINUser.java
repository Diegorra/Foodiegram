package main.domain.resource;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.annotation.Resource;

@Resource
@Data
@AllArgsConstructor
public class PreviewColabJOINUser {

    private String name;
    private String origin;
    private String type;
    private String pais;
    private String ciudad;
    private String calle;
    private String image;
}
