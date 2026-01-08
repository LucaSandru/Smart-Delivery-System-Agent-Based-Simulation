# Smart Delivery System (Agent-Based Simulation using JADE)

This project focuses on an intelligent, multi-agent delivery coordination system simulating real-world logistics, built with the JADE framework in Java.

---

## Project Overview

**Smart Delivery System** models a smart logistics environment where intelligent agents interact autonomously to facilitate package delivery. It simulates real-time negotiation between customers, couriers, and a dispatcher to choose the most optimal delivery path based on price, distance, and urgency.

The project was developed to explore **Agent-Oriented Programming (AOP)** using **JADE (Java Agent DEvelopment)** framework — very common used in multi-agent research and real-time distributed systems.

---

## Agent Architecture

The system consists of three agent types:

- **CustomerAgent** – Requests a delivery, specifies package details, and confirms offers.
- **CourierAgent(s)** – Respond with price & time quotes based on the delivery request.
- **DispatcherAgent** – Acts as a coordinator: forwards requests, collects offers, picks the best, and manages confirmation flow.

Multiple courier agents can be deployed dynamically to simulate market competition (via JADE Graphical User Interface)

---

## Installation Instructions

### Prerequisites

- Java JDK 8 or higher  
- Eclipse or IntelliJ IDEA 
- JADE Framework (Java Agent DEvelopment Framework) 

### Steps

1. **Clone the repository**

   ```bash
   git clone https://github.com/your-username/smart-delivery-system.git

2. **Open the project in your IDE**
   Import the project as a *Java Project* On IntelliJ or Eclipse

3. **Add the JADE library**
   - Download `jade.jar` from: [https://jade.tilab.com/]
   - In your IDE, add to the project's *Java Build Path* &rarr; *Libraries* &rarr; *Classpath*
   - The *JADE* platform will start, showing the GUI.

4. **Start the JADE platform with GUI**
   From terminal or IDE configuration:
   ```bash
   java -cp jade.jar jade.Boot -gui ```

5. **Create agents from the JADE GUI**
   The system automatically starts: (shown also in Main-Container GUI)
- `dispatcher` agent
- `courier1` and `courier2` agents

From the GUI, user only need to manually create the **customer agent**: (there is a `Usage` printed in the console)
- *Name:* any (e.g. `Luca`)
- *Class:* `agents.CustomerAgent` - has to be chosen from the list of classes
- *Arguments:* Package1 Center 5 20 (High), where:
    - `Package1` = package ID, `Center` = destination, `5` = weight in kg, `20` = distance in km, `High` = urgency level - optional)

Note on **urgency level**:
- The urgency level affects the delivery offered price by the *courier1* and *courier2* agents.
- Available options:
  - `High` &rarr; Increases price (faster delivery assumed)
  - `Normal` (default) &rarr; Standard rate
  - `Low` &rarr; Discounted offer

---

## Communication Flow

1. **Customer** sends a delivery request: package ID, destination, weight, distance, urgency.
2. **Dispatcher** receives the request and forwards it to all available *Courier agents*.
3. **Couriers** respond with dynamically generated offers (price + estimated time).
4. **Dispatcher** evaluates and sends the *best offer* back to the customer.
5. *Customer* confirms or rejects the offer (with live console interaction).
6. On confirmation, the *Dispatcher* notifies the selected *Courier* to proceed.

> Features console-based confirmation input for user, with validation loop (`Yes/No`).
> Includes realistic price/time calculation logic using urgency, weight, and distance.

---

## Technologies Used

- **Java** = Core language for logic and agents
- **JADE** = Agent-based framework (Java Agent DEvelopment)
- **Eclipse** = IDE for coding and support
- **ACL Messages** = Agent Communication Language

---

## Sample Output (example)

(First, let's create on JADE GUI an `agents.customer` agent, with `name = Luca` and for `arguments = 'P1 Center 5 20'` (arguments explained also in `Usage`)

- Luca: Sending delivery request for package P1, to destination 'Center', with a weight of 5.0 kg and distance of 20.0 km...
- CustomerAgent: Delivery request sent.
- dispatcher: Forwarding request to couriers...
- courier1: Sent offer -> Price: 37.28 | Time: 2h
- courier2: Sent offer -> Price: 39.55 | Time: 3h
- dispatcher: Sent best offer to Luca
- dispatcher: Asking customer for confirmation...
- CustomerAgent: Received best offer from courier1 -> Price: 37.28 | Time: 2h
- Do you confirm the delivery? (Yes/No): Yes
- CustomerAgent: Sent confirmation: YES
- dispatcher: Customer confirmed. Notifying courier1
- courier1: Delivery confirmed. Heading to destination...

---

## Project architecture

- src/
    - agents/
        - CustomerAgent.java   &rarr; Sends delivery request with package info + confirms the offer with console input
        - CourierAgent.java    &rarr; Calculates and sends delivery offers (price & time) in response to requests
        - DispatcherAgent.java    &rarr; Coordinates communication: forwards request, collects offers, asks for confirmation
    - ontology/
        - DeliveryRequest.java    &rarr; Ontology class defining fields for a delivery request (packageId, destination, etc.)
        - DeliveryOffer.java    &rarr; Ontology class defining a courier's offer (price, estimated time, courier name)
        - ConfirmationRequest.java    &rarr; Ontology class used by dispatcher to ask the customer for confirmation
        - ConfirmationResponse.java    &rarr; Ontology class for customer's reply to the confirmation request (yes/no)
        - DeliveryOntology.java    &rarr; Registers all ontology classes with JADE for structured agent communication
      
