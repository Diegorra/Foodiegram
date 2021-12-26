package main.domain.converter;


import main.domain.resource.ComentarioResource;
import main.persistence.entity.Comentario;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ComentarioConverter implements Converter<Comentario, ComentarioResource> {

    @Override
    public ComentarioResource convert(Comentario source){

        if (source == null)
            return null;

        ComentarioResource response=new ComentarioResource();
        response.setId(source.getId());
        response.setIdpubli(source.getIdpubli());
        response.setIduser(source.getAutor().getId());
        response.setText(source.getText());
        response.setPfp(source.getAutor().getImage());
        response.setUser(source.getAutor().getName());
        return response;

    }

}
