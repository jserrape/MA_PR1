/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tareas;

import agentes.AgenteFormulario;
import agentes.AgenteOperacion;
import jade.core.AID;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author jcsp0003
 */
public class enviarAConsola extends TickerBehaviour {

    private AgenteFormulario agF;
    private AgenteOperacion agO;
    private String tipoAgente;
    private AID aid;

    public enviarAConsola(Agent a, long period, String tipoAgentee, AgenteFormulario agFF) {
        super(a, period);
        this.tipoAgente = tipoAgentee;
        this.agF = agFF;
        this.aid = myAgent.getAID();
    }

    public enviarAConsola(Agent a, long period, String tipoAgentee, AgenteOperacion agOO) {
        super(a, period);
        this.tipoAgente = tipoAgentee;
        this.agO = agOO;
        this.aid = myAgent.getAID();
    }

    @Override
    protected void onTick() {
        ACLMessage mensaje;
        AID receptor = null;
        boolean notNULL = false, tienesMenPendientes = false;
        String mensajeP = null;

        if ("AgenteOperacion".equals(tipoAgente)) {
            if (agO.getAgentesConsola() != null) {
                notNULL = true;
                if (!agO.getMensajesPendientes().isEmpty()) {
                    tienesMenPendientes = true;
                    receptor = agO.getAgentesConsola()[0];
                    mensajeP = agO.getMensajesPendientes().remove(0);
                }
            }
        } else {
            if ("AgenteFormulario".equals(tipoAgente)) {
                if (agF.getAgentesConsola() != null) {
                    notNULL = true;
                    if (!agF.getMensajesPendientes().isEmpty()) {
                        tienesMenPendientes = true;
                        receptor = agF.getAgentesConsola()[0];
                        mensajeP = agF.getMensajesPendientes().remove(0);
                    }
                }
            }
        }
        if (notNULL) {
            if (tienesMenPendientes) {
                mensaje = new ACLMessage(ACLMessage.INFORM);
                mensaje.setSender(this.aid);
                mensaje.addReceiver(receptor);
                mensaje.setContent(mensajeP);

                myAgent.send(mensaje);
            } else {
                //Acciones que queremos hacer si no tenemos
                //mensajes pendientes
            }
        }
    }

}
