/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes;

import GUI.ConsolaJFrame;
import tareas.buscaAgente;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.ArrayList;
import java.util.Random;
import tareas.enviarAConsola;
import utilidad.Punto2D;

/**
 *
 */
public class AgenteOperacion extends Agent {

    private AID[] agentesConsola;
    private ArrayList<String> mensajesPendientes;
    private ArrayList<Punto2D> operacionesPendientes;
    private Random rnd;

    private ConsolaJFrame gui;
    
    @Override
    protected void setup() {
        //Inicialización de variables
        mensajesPendientes = new ArrayList();
        operacionesPendientes = new ArrayList();
        rnd = new Random();
        gui = new ConsolaJFrame(this.getName());

        //Registro en páginas Amarrillas
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Utilidad");
        sd.setName("Operacion");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        //Regisro de la Ontología
        //Añadir las tareas principales
        addBehaviour(new buscaAgente(this, 5000, "Consola", this, "AgenteOperacion"));
        addBehaviour(new TareaRecepcionOperacion());
        addBehaviour(new TareaRecepcionRespuesta());
        addBehaviour(new enviarAConsola(this, 10000, "AgenteOperacion", this));
    }

    public void copiaListaConsola(AID[] agentes, int tam) {
        agentesConsola = new AID[tam];
        System.arraycopy(agentes, 0, agentesConsola, 0, tam);
    }

    public void listaConsolaNull() {
        agentesConsola = null;
    }

    public AID[] getAgentesConsola() {
        return agentesConsola;
    }

    public ArrayList<String> getMensajesPendientes() {
        return mensajesPendientes;
    }

    @Override
    protected void takeDown() {
        //Desregistro de las Páginas Amarillas
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        //Se liberan los recuros y se despide
        System.out.println("Finaliza la ejecución de " + this.getName());
    }

    private String operacion(Punto2D punto) {
        double resultado;

        //Se realiza una operación elegida de forma aleatoria
        int i = rnd.nextInt(4);

        switch (i) {
            case 0:
                //Suma
                resultado = punto.getX() + punto.getY();
                return "Se ha realizado la suma de " + punto
                        + "\ncon el resultado: " + resultado;
            case 1:
                //Resta
                resultado = punto.getX() - punto.getY();
                return "Se ha realizado la resta de " + punto
                        + "\ncon el resultado: " + resultado;
            default:
                //Multiplicación
                resultado = punto.getX() * punto.getY();
                return "Se ha realizado la multiplicación de " + punto
                        + "\ncon el resultado: " + resultado;
        }

    }

    public class TareaEnviarRespuesta extends OneShotBehaviour {

        private ACLMessage mensaje;

        public TareaEnviarRespuesta(ACLMessage mensajee) {
            this.mensaje = mensajee;
        }

        @Override
        public void action() {
            ACLMessage respuesta = mensaje.createReply();
            respuesta.setPerformative(ACLMessage.CONFIRM);
            respuesta.setContent("El agente " + this.getAgent().getName() + " ha recibido el mensaje de " + mensaje.getSender().getName());
            send(respuesta);
        }

    }

    public class TareaRecepcionOperacion extends CyclicBehaviour {

        @Override
        public void action() {
            //Recepción de la información para realizar la operación
            MessageTemplate plantilla = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage mensaje = myAgent.receive(plantilla);

            if (mensaje != null) {
                //procesamos el mensaje
                String[] contenido = mensaje.getContent().split(",");

                Punto2D punto = new Punto2D();
                punto.setX(Double.parseDouble(contenido[0]));
                punto.setY(Double.parseDouble(contenido[1]));

                operacionesPendientes.add(punto);

                addBehaviour(new TareaEnviarRespuesta(mensaje));
                addBehaviour(new TareaRealizarOperacion());

            } else {
                block();
            }
        }
    }

    public class TareaRealizarOperacion extends OneShotBehaviour {

        @Override
        public void action() {
            //Realizar una operacion pendiente y añadir el mensaje
            //para la consola
            Punto2D punto = operacionesPendientes.remove(0);
            mensajesPendientes.add(operacion(punto));
        }
    }

    public class TareaRecepcionRespuesta extends CyclicBehaviour {

        @Override
        public void action() {
            //Recepción de la respuesta
            MessageTemplate plantilla = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);
            ACLMessage mensaje = myAgent.receive(plantilla);

            if (mensaje != null) {
                gui.presentarSalida("- " + mensaje.getContent());
            } else {
                block();
            }
        }
    }
}
