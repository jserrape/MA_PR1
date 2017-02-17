/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tareas;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 *
 * @author admin
 */
    /**
     * Tarea que revisa si hay confirmaciones de mensajes enviados
     */
    public class TareaRecepcionRespuesta extends CyclicBehaviour {

        @Override
        public void action() {
            //Recepción de la respuesta
            MessageTemplate plantilla = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);
            ACLMessage mensaje = myAgent.receive(plantilla);

            if (mensaje != null) {
                System.out.println(mensaje.getContent());
            } else {
                block();
            }
        }
    }