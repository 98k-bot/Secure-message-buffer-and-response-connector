# :shipit:Secure-message-buffer-and-response-connector:shipit:

**The Basic Structure of the Message buffer & response connector:**

1. monitor MessageBuffer&Response
Encapsulates a message buffer that holds at most one message -- and a response buffer that holds at most one response.
Monitor operations are executed mutually exclusively.
private messageBufferFull : Boolean = false; 
private responseBufferFull : Boolean = false; 

- public send (in message, out response)
place message in buffer; messageBufferFull := true;
signal;
while responseBufferFull = false do wait; 
remove response from response buffer; 
responseBufferFull := false;
end send;

- public receive (out message)
while messageBufferFull = false do wait; 
remove message from buffer; 
messageBufferFull := false;

- public reply (in response)
Place response in response buffer; responseBufferFull := true;
end receive;
signal; end reply;
end MessageBuffer&Response;
end receive;

![image](https://github.com/98k-bot/Secure-message-buffer-and-response-connector/blob/branch1/MBRC_Structure.png)

**For the security consideration, each message from a sender object to a receiver object requires confidentiality security, whereas each response from the receiver object to the sender object requires integrity security.**

The results of running this program with Apache NetBeans 11.3.
![image](https://github.com/98k-bot/Secure-message-buffer-and-response-connector/blob/branch1/snapshot1.png)
![image](https://github.com/98k-bot/Secure-message-buffer-and-response-connector/blob/branch1/snapshot2.png)
![image](https://github.com/98k-bot/Secure-message-buffer-and-response-connector/blob/branch1/snapshot3.png)

