# Knewin Crawler
#### Este é um projeto de web crawling em Java para captura de dados de notícias sobre o mercado financeiro.

Feito com o objetivo de capturar dados das últimas notícias publicadas no site [infomoney](https://www.infomoney.com.br/mercados/), o crawler interage com o navegador e imprime os dados de título, subtítulo, autor, data de publicação, url e conteúdo de cada publicação.

Os testes deste crawler foram realizados utilizando um navegador google chrome versão 84.

Para sua utilização, o caminho para o arquivo **chromedriver.exe** deverá ser modificado no arquivo **config.properties** contido no diretório **/src**.

Obs.: Caso não possua o arquivo, verifique a versão do seu navegador e com base nisso realize o download da versão adequada para ele [aqui](https://chromedriver.chromium.org/downloads).

&nbsp;
---
&nbsp;
### Total de notícias

Como visto no trecho abaixo, a quantidade de notícias capturadas pode ser modificada apenas com a alteração do parâmetro passado no método construtor do Crawler, sendo que o valor 3 é definido por padrão.
~~~    
public static void main(String[] args) {
		
	Crawler crawler = new Crawler(3);
	crawler.start();
		
}
~~~
&nbsp;

<p align="center">
<img src="https://cdn2.iconfinder.com/data/icons/web-data-and-scraping/64/data--scraping-19-512.png" width="100">
</p>
