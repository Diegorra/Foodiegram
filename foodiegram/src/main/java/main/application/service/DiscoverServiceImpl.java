package main.application.service;


import main.domain.converter.PreviewColabJOINUserConverter;
import main.domain.converter.PreviewPublicacionConverter;
import main.domain.converter.PreviewUserConverter;
import main.domain.resource.PreviewColabJOINUser;
import main.domain.resource.PreviewPublicacion;
import main.domain.resource.PreviewUsuario;
import main.persistence.entity.ColabJOINUser;
import main.persistence.entity.Publicacion;
import main.persistence.entity.Usuario;
import main.persistence.repository.RepoColabJOINUser;
import main.persistence.repository.RepoPublicacion;
import main.persistence.repository.RepoUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscoverServiceImpl implements DiscoverService {

    @Autowired
    private RepoPublicacion repoPublicacion;

    @Autowired
    private RepoUsuario repoUsuario;

    @Autowired
    private RepoColabJOINUser repoColabJOINUser;

    private PreviewColabJOINUserConverter previewColabJOINUserConverter= new PreviewColabJOINUserConverter();
    private PreviewUserConverter usuarioConverter = new PreviewUserConverter();
    private PreviewPublicacionConverter publicacionConverter = new PreviewPublicacionConverter();


    public List<PreviewPublicacion> discoverByAmigo(){

        Integer userid=Integer.parseInt(SecurityContextHolder.getContext().getAuthentication().getName());

        List<Publicacion> publi = repoPublicacion.fromFriends(userid);

        if(publi==null)
            return  null;

        return publi.stream().map(publicacionConverter::convert).collect(Collectors.toList());

    }


    public List<PreviewPublicacion> discoverBestRated(String period, String country, String city) throws IllegalArgumentException{

        List<Publicacion> publi = repoPublicacion.bestRated(getDayAmount(period), country, city);

        if(publi==null)
            return  null;

        return publi.stream().map(publicacionConverter::convert).collect(Collectors.toList());
    }

    @Override
    public List<PreviewPublicacion> discoverMostRated(String period, String country, String city) {

        List<Publicacion> publi = repoPublicacion.mostRated(getDayAmount(period),  country, city);

        if(publi==null)
            return  null;

        return publi.stream().map(publicacionConverter::convert).collect(Collectors.toList());
    }

    @Override
    public List<PreviewUsuario> findFollowedByFriends(Integer userid) {
        List<Usuario> list = repoUsuario.findFollowedByFriends(userid);

        return list.stream().map(usuarioConverter::convert).collect(Collectors.toList());
    }

    @Override
    public List<PreviewUsuario> userWhoFollowXAlsoFollowY(String userName) {

        Usuario user = repoUsuario.findByName(userName);

        if (user == null)
            return null;

        List<Usuario> list = repoUsuario.usersFollowedByUsersWhoFollow(user.getId());

        return list.stream().map(usuarioConverter::convert).collect(Collectors.toList());
    }

    @Override
    public List<PreviewColabJOINUser> findCollabs(String country, String city) {

        List<ColabJOINUser> collab=repoColabJOINUser.descubrirCollab(country,city);

        return collab.stream().map(previewColabJOINUserConverter::convert).collect(Collectors.toList());
    }


    private Integer getDayAmount(String periodo) {

        switch (periodo) {
            case "day": return 1;
            case "week": return 7;
            case "month": return 30;
            case "year": return 365;
            case "allTime": return -1;
            default: throw new IllegalArgumentException("Invalid period");
        }


    }


}
