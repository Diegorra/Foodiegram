package main.application.service.manageAccountService;

import main.domain.converter.AmigoConverter;
import main.domain.converter.PreviewPublicacionConverter;
import main.domain.resource.AmigoResource;
import main.domain.resource.PreviewPublicacion;
import main.persistence.IDs.IDamigo;
import main.persistence.entity.Amigo;
import main.persistence.entity.Publicacion;
import main.persistence.entity.Usuario;
import main.persistence.repository.RepoAmigo;
import main.persistence.repository.RepoPublicacion;
import main.persistence.repository.RepoUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManageFriendsImpl implements ManageFriends{


    private final AmigoConverter friendConverter = new AmigoConverter();

    PreviewPublicacionConverter converterPreview = new PreviewPublicacionConverter();

    @Autowired
    RepoUsuario repoUser;

    @Autowired
    RepoAmigo repoAmigo;

    @Autowired
    RepoPublicacion repoPost;

    @Override
    public AmigoResource addFriend(Integer id, String name)throws IllegalArgumentException {

        Usuario user  = repoUser.findByName(name);

        if(user == null || user.getId() == id) //comprobamos que el usuario existe
            throw new IllegalArgumentException("There is not a user with name: " + name);
        else{
            List<String> friends = getFriends(id);
            if(!friends.contains(name)){
                Amigo friend = new Amigo(id, user.getId());
                repoAmigo.save(friend);
                return friendConverter.convert(friend);
            }else{
                throw new IllegalArgumentException("You are already friends");
            }
        }
    }

    @Override
    public AmigoResource removeFriend(Integer id, String name) throws IllegalArgumentException{
        Usuario user = repoUser.findByName(name);

        if(user == null)//comprobamos que el usuario existe
            throw new IllegalArgumentException("There is not a user with name: " + name);
        else{
            Amigo friend1 = repoAmigo.findOne(new IDamigo(id, user.getId())); //comprobamos que son amigos
            Amigo friend2 = repoAmigo.findOne(new IDamigo(user.getId(), id));

            if(friend1 == null && friend2 != null){
                repoAmigo.delete(friend2);
                return friendConverter.convert(friend2);
            }
            else if(friend1 != null && friend2 == null){
                repoAmigo.delete(friend1);
                return friendConverter.convert(friend1);
            }
            else{
                throw new IllegalArgumentException("You have no friend with name: " + name);
            }

        }
    }

    @Override
    public List<String> getFriends(Integer id){
        Usuario user = repoUser.findOne(id);

        if(user == null) return null;
        else{
            List<Amigo> friends = repoAmigo.findAll();
            List<String> friendsName = new ArrayList<>();
            for(Amigo friend : friends){
                if(user.getId() == friend.getIduser1()){
                    friendsName.add(repoUser.findOne(friend.getIduser2()).getName());
                }

            }
            return friendsName;
        }
    }

    @Override
    public List<PreviewPublicacion> viewPostOfFriend(Integer id, String name) throws IllegalArgumentException{
        Usuario user = repoUser.findByName(name);

        if(user == null) //comprobamos que el usuario existe
            return null;
        else{
            Amigo friend1 = repoAmigo.findOne(new IDamigo(id, user.getId()));
            Amigo friend2 = repoAmigo.findOne(new IDamigo(user.getId(), id));

            if(friend1 == null && friend2 == null)//comprobamos que son amigos, sino, no podrá ver sus fotos
                throw new IllegalArgumentException("You have no friend with name: " + name);
            else{
                List<Publicacion> post = repoPost.findByIduserOrderByIdDesc(user.getId());
                return post.stream().map(converterPreview::convert).collect(Collectors.toList());
            }
        }
    }
}
