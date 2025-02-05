package com.accenture.banco.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.accenture.banco.entity.Cliente;
import com.accenture.banco.entity.ContaCorrente;
import com.accenture.banco.service.ClienteService;
import com.accenture.banco.service.ContaCorrenteService;
import com.accenture.banco.util.FullBalance;
import com.accenture.banco.util.FullInOutBalance;
import com.accenture.banco.util.InOutBalance;
import com.accenture.banco.util.Valor;

@RestController
@RequestMapping("/conta")
public class ContaCorrenteController {
	
	@Autowired
	private ContaCorrenteService contaCorrenteService;
	
	@Autowired
	private ClienteService clienteService;
	
	//Listar Contas Correntes
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<List<ContaCorrente>> listarContasCorrentes(){
		List<ContaCorrente> contascorrentes = contaCorrenteService.listaTodasContas();
		//se a requisicao for ok() 200 - entao retornamos alunos no body
		return ResponseEntity.ok().body(contascorrentes);
	}	
	
	//Pesquisar Contas pelo id do cliente
	@RequestMapping(value="/{id}", method = RequestMethod.GET)
	public ResponseEntity<Optional> buscaPorCpf(@PathVariable int id) throws ObjectNotFoundException{
		try {
			Cliente cliente = clienteService.buscarClientePorId(id);
			List<ContaCorrente> contasCorrentes = contaCorrenteService.buscarContasPorCliente(cliente);
			return ResponseEntity.ok().body(Optional.ofNullable(contasCorrentes));
		}catch(ObjectNotFoundException e){
			return ResponseEntity.badRequest().body(Optional.ofNullable(e.getMessage()));
		}catch(Exception e){
			return ResponseEntity.badRequest().body(Optional.ofNullable(e.getMessage()));
		}
	}
	
	//Pesquisar Contas pelo cpf do cliente
	@RequestMapping(value="/search/{cpf}", method = RequestMethod.GET)
	public ResponseEntity<Optional> buscaPorCpf(@PathVariable String cpf) throws ObjectNotFoundException{
		try {
			Cliente cliente = clienteService.buscarClientePorCpf(cpf);
			List<ContaCorrente> contasCorrentes = contaCorrenteService.buscarContasPorCliente(cliente);
			return ResponseEntity.ok().body(Optional.ofNullable(contasCorrentes));
		}catch(ObjectNotFoundException e){
			return ResponseEntity.badRequest().body(Optional.ofNullable(e.getMessage()));
		}catch(Exception e){
			return ResponseEntity.badRequest().body(Optional.ofNullable(e.getMessage()));
		}
	}
	
	//Balanço da(s) conta(s) - por clientId
	@RequestMapping(value="/balance/{idClient}", method = RequestMethod.GET)
	public ResponseEntity<Optional> getBalance(@PathVariable int idClient) throws ObjectNotFoundException{
		try {
			Cliente cliente = clienteService.buscarClientePorId(idClient);
			List<ContaCorrente> contasCorrentes = contaCorrenteService.buscarContasPorCliente(cliente);
			double balance = contaCorrenteService.getBalance(contasCorrentes);
			return ResponseEntity.ok().body(Optional.ofNullable(balance));
		}catch(ObjectNotFoundException e){
			return ResponseEntity.badRequest().body(Optional.ofNullable(e.getMessage()));
		}catch(Exception e){
			return ResponseEntity.badRequest().body(Optional.ofNullable(e.getMessage()));
		}
	}
	
	//Balanço Completo da(s) conta(s) - por clientId
	@RequestMapping(value="/fullbalance/{idClient}", method = RequestMethod.GET)
	public ResponseEntity<Optional> getFullBalance(@PathVariable int idClient) throws ObjectNotFoundException{
		try {
			Cliente cliente = clienteService.buscarClientePorId(idClient);
			List<ContaCorrente> contasCorrentes = contaCorrenteService.buscarContasPorCliente(cliente);
			FullBalance balance = contaCorrenteService.getFullBalance(contasCorrentes);
			return ResponseEntity.ok().body(Optional.ofNullable(balance));
		}catch(ObjectNotFoundException e){
			return ResponseEntity.badRequest().body(Optional.ofNullable(e.getMessage()));
		}catch(Exception e){
			return ResponseEntity.badRequest().body(Optional.ofNullable(e.getMessage()));
		}
	}
	
	//Entradas e saidas da(s) conta(s) - por clientId
	@RequestMapping(value="/inout/{idClient}", method = RequestMethod.GET)
	public ResponseEntity<Optional> inOutBalance(@PathVariable int idClient) throws ObjectNotFoundException{
		try {
			Cliente cliente = clienteService.buscarClientePorId(idClient);
			List<ContaCorrente> contasCorrentes = contaCorrenteService.buscarContasPorCliente(cliente);
			InOutBalance balance = contaCorrenteService.getInOutBalance(contasCorrentes);
			return ResponseEntity.ok().body(Optional.ofNullable(balance));
		}catch(ObjectNotFoundException e){
			return ResponseEntity.badRequest().body(Optional.ofNullable(e.getMessage()));
		}catch(Exception e){
			return ResponseEntity.badRequest().body(Optional.ofNullable(e.getMessage()));
		}
	}
	
	//Entradas e saidas COMPLETO da(s) conta(s) - por clientId
	@RequestMapping(value="/fullinout/{idClient}", method = RequestMethod.GET)
	public ResponseEntity<Optional> fullInOutBalance(@PathVariable int idClient) throws ObjectNotFoundException{
		try {
			Cliente cliente = clienteService.buscarClientePorId(idClient);
			List<ContaCorrente> contasCorrentes = contaCorrenteService.buscarContasPorCliente(cliente);
			FullInOutBalance balance = contaCorrenteService.getFullInOutBalance(contasCorrentes);
			return ResponseEntity.ok().body(Optional.ofNullable(balance));
		}catch(ObjectNotFoundException e){
			return ResponseEntity.badRequest().body(Optional.ofNullable(e.getMessage()));
		}catch(Exception e){
			return ResponseEntity.badRequest().body(Optional.ofNullable(e.getMessage()));
		}
	}
	
	//Cadastrar Conta Corrente
	@RequestMapping(value="/{idClient}", method = RequestMethod.POST)
	public ResponseEntity<String> inserircliente(@PathVariable int idClient) {
		try {
			
			if(!clienteService.checkExistingClient(idClient)) throw new Exception("Cliente inexistente!");
			
			Cliente cliente = clienteService.buscarClientePorId(idClient);
			
			ContaCorrente cc = new ContaCorrente();
			cc.setCliente(cliente);
			cc.setContaCorrenteSaldo(0.0);
			ContaCorrente contacorrente = contaCorrenteService.salvar(cc);
			URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(contacorrente.getIdContaCorrente()).toUri();
			return ResponseEntity.created(uri).build();
		}catch(Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	//deposito
	@RequestMapping(value="/deposito/{id}", method = RequestMethod.PUT)
	public ResponseEntity<String> depositar(@RequestBody Valor objBody, @PathVariable Integer id) throws Exception {
		try {
			objBody.setValor(Math.abs(objBody.getValor()));
			objBody.setId(id);
			return contaCorrenteService.deposito(objBody);
		}catch(Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	//transferencia
	@RequestMapping(value="/transferencia/{idOrigem}/{idDestino}", method = RequestMethod.PUT)
	public ResponseEntity<String> depositar(@RequestBody Valor objBody, @PathVariable Integer idOrigem, @PathVariable Integer idDestino) throws Exception {
		try {
			objBody.setValor(Math.abs(objBody.getValor()));
			objBody.setId(idOrigem);
			return contaCorrenteService.transferencia(objBody, idDestino);
		}catch(Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	//saque
	@RequestMapping(value="/saque/{id}", method = RequestMethod.PUT)
	public ResponseEntity<String> sacar(@RequestBody Valor objBody, @PathVariable Integer id) {
		try{
			objBody.setValor(Math.abs(objBody.getValor()));
			objBody.setId(id);
			return contaCorrenteService.saque(objBody);
		}catch(NumberFormatException e){
			return ResponseEntity.badRequest().body(e.getMessage());
		}catch(Exception e){
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	/*
	@RequestMapping(method =  RequestMethod.POST)
    public Agencia Post(@Validated @RequestBody Agencia agencia)
    {
        return agenciaService.salvar(agencia);
    }
	*/
	
}
