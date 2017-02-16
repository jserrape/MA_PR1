/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tareas;

import agentes.AgenteOperacion;


import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

/**
 *
 * @author jcsp0003
 */
public class buscaAgente extends TickerBehaviour {

    private AID[] agentes;
    private String tipo;
    private String tipoAgente;

    private AgenteOperacion agO;

    public buscaAgente(Agent a, long period, String tipoo, AgenteOperacion agOO, String tipoAgentee) {
        super(a, period);
        this.tipo = tipoo;
        this.agO = agOO;
        this.tipo = tipoo;
        this.tipoAgente = tipoAgentee;
    }

    @Override
    protected void onTick() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setName(this.tipo);
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(myAgent, template);
            if (result.length > 0) {
                agentes = new AID[result.length];
                for (int i = 0; i < result.length; ++i) {
                    agentes[i] = result[i].getName();
                }
                if ("AgenteOperacion".equals(this.tipoAgente)) {
                    agO.copiaListaConsola(agentes, result.length);
                }
            } else {
                //No se han encontrado agentes
                agentes = null;
                if ("AgenteOperacion".equals(this.tipoAgente)) {
                    agO.listaConsolaNull();
                }
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
}
