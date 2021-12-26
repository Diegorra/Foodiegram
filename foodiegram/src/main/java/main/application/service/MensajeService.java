package main.application.service;

import main.domain.resource.MensajeResource;
import main.rest.forms.MessageForm;

import javax.naming.NoPermissionException;
import java.util.List;

public interface MensajeService {


    //Devuelve el MesajeResource con el mensaje que se elimina (buscado por el mensID),si el mensaje no existe retorna null.
    MensajeResource deleteMensaje(Integer mensID) throws NoPermissionException;

       //Devuelve un MensajeResource con el mensaje posteado, si alguno de los dos usuarios no exiten salta la exeption.
    MensajeResource setMensaje(Integer userID, MessageForm mensaje) throws IllegalArgumentException;

    //Devuelve una lista de MensajeResource con los mensajes que ha enviado el user1ID.
    List<MensajeResource> getMensajes(Integer userID);



}
