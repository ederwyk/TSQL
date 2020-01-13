TSQL
=======

Biblioteca para geração de SQL a partir de entidades mapeadas por plano.

Tem por objetivo normalizar as queries para os diversos bancos com diferentes nomenclaturas.

####Classe Mapeada:

```java

import static br.com.stockinfo.stockplanos.DataSourcePlanos.*;

@Table(name = "Participante_web", dataSource = {METRUSFAMILIA, OABPREVRJ})
public class Participante {

	@Column(name = "id", dataSource = METRUSFAMILIA)
	@Column(name = "id_participante", dataSource = OABPREVRJ)
	private Integer idParticipante;

	@Column(snake = true, dataSource = OABPREVRJ)
	private Integer idCorretor;

	// Mapeamento de multiplos planos
	@Column(dataSource = {METRUSFAMILIA, OABPREVRJ})
	private String sexo;

	@Column(snake = true, dataSource = OABPREVRJ)
	private String nomeProponente;

	@Column(name = "num_proposta", dataSource = OABPREVRJ)
	private String numeroProposta;
	
	// Sem anotação, sem mapeamento
	private String telefone;
}

```

#####@Column: 
Atributo para associar o campo da entidade ao campo da tabela. 
Se utilizado sem parametros, o nome do campo será identico ao nome do campo na tabela. 
Utilizando 'snake = true', o campo ira ficar no formato snake_case. 
Se utilizar o parâmetro 'name', 'snake' será ignorado e o campo será associado ao nome mencionado. 
O parâmetro 'dataSource' é obrigatório.

#####@Table:
Atributo para associar entidade a uma tabela no banco. Se utilizar o parâmetro 'name', a entidade será associada a tabela com o nome mencionado.
O parâmetro 'dataSource' é obrigatório.

#### Exemplos de query:

#####Select:
```java
Select.from(Participante.class)
    .where(Participante::getId).eq(10)
    .where(Participante::getDataNascimento).gt(new Date())
```

##### Select com join (exemplo utilizando o client já instanciado)

```java
client.plano(Select.from(Beneficio.class)
    .project(Beneficio::getNome)
    .where(Beneficio::getIdBeneficio).eq(10)
    .where(Beneficio::getDataNascimento).between(new Date(), new Date());
    .leftJoin(TipoBeneficio.class).on(Beneficio::getIdTipoBeneficio, TipoBeneficio::getId)
    .where(TipoBeneficio::getDescricao).eq("exemplo")
    .using(Beneficio.class)
);
```
Tipos de joins:
* .join (inner join)
* .leftJoin
* .rightJoin
* .outerJoin

##### Project com função
```java
.project(CadcobCpf::getValorBruto, Func.SUM)
```

Tipos de funções:

* Func.SUM
* Func.MAX
* Func.MIN
* Func.COALESCE

##### Where com Modifiyer
```java
.where(Participante::getNomeProponente, Modfyer.LOWER).eq("eder")
```

Tipos de Modifiyer:
* Modifiyer.LOWER
* Modifiyer.UPPER

##### Update/Insert

```java
Participante participante = new Participante();
participante.setNome("Teste");

//Para gerar SQL com apenas alguns campos, use .set
Insert.entity(participante).set(Participante::getNome);

// Para gerar SQL com todos os atributos não nulos
Insert.entity(participante)

// Para gerar SQL com os atributos nulos
Insert.entity(participante).allowNull()

// Para gerar SQL sem os atributos vazios
Insert.entity(participante).rejectEmpty()
```

O uso do Insert é igual ao do Update
