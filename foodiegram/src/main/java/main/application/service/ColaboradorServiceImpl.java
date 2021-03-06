package main.application.service;


import main.domain.converter.ColaboradorConverter;
import main.domain.resource.ColaboradorResource;
import main.persistence.entity.Colaborador;
import main.persistence.entity.RoleEnum;
import main.persistence.entity.Usuario;
import main.persistence.repository.RepoColaborador;
import main.persistence.repository.RepoUsuario;
import main.rest.forms.CollaborateForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ColaboradorServiceImpl implements ColaboradorService {

    private ColaboradorConverter converterCol = new ColaboradorConverter();

    @Autowired
    private RepoUsuario repoUsuario;

    @Autowired
    private RepoColaborador repoColaborador;

    @Autowired
    private RestService restService;

    @Override
    public ColaboradorResource upgradeUser(Integer userid, CollaborateForm form) {

        String country=null;
        String city=null;
        String street= null;


        Usuario user = repoUsuario.findOne(userid);

        if (form.getLatitud() != null && form.getLongitud() != null) {
            Map<String, Object> geoData = restService.getGeoData(form.getLatitud(), form.getLongitud());
            try {
                country = geoData.get("country").toString();
                city = geoData.get("city").toString();
                street=geoData.get("street").toString();
            }

            catch (NullPointerException ignored) {

            }
        }

        Colaborador colaborador=new Colaborador(user.getId(), form.getOrigin(), form.getType(), country,city,street);
        user.setRole(RoleEnum.ROLE_COL);
        repoUsuario.save(user);
        repoColaborador.save(colaborador);

        return converterCol.convert(colaborador);
    }

    @Override
    public ColaboradorResource getCollab(Integer User) {
        return converterCol.convert(repoColaborador.findOne(User));
    }


}
