SET SQL_MODE = '';

DROP TABLE IF EXISTS sqldados.T_EMP;
CREATE TEMPORARY TABLE sqldados.T_EMP (
  PRIMARY KEY (empno)
)
SELECT no                                                            AS empno,
       name                                                          AS empName,
       sname                                                         AS empSname,
       trim(cast(CONCAT(CHAR(ascii(mid(pswd, 1, 1)) - 5), CHAR(ascii(mid(pswd, 2, 1)) - 7),
			CHAR(ascii(mid(pswd, 3, 1)) - 8), CHAR(ascii(mid(pswd, 4, 1)) - 0),
			CHAR(ascii(mid(pswd, 5, 1)) - 34), CHAR(ascii(mid(pswd, 6, 1)) - 9),
			CHAR(ascii(mid(pswd, 7, 1)) - 9),
			CHAR(ascii(mid(pswd, 8, 1)) - 13)) AS CHAR)) AS senha
FROM sqldados.emp
WHERE pswd <> '';

DROP TEMPORARY TABLE IF EXISTS sqldados.TPED;
CREATE TEMPORARY TABLE sqldados.TPED (
  PRIMARY KEY (storeno, ordno)
)
SELECT eord.storeno,
       eord.ordno,
       CONCAT(eord.nfno, '/', eord.nfse)                                                AS NFiscal,
       CONCAT(eord.nfno_futura, '/', eord.nfse_futura)                                  AS NF_Fat,
       eord.amount                                                                      AS valor,
       IF(eord.other = 0, MID(eordrk.remarks__480, 7, 10), eord.other) / 100            AS frete,
       UPPER(TRIM(MID(eordrk.remarks__480, 160, 40)))                                   AS DEPOSITANTE,
       (eord.amount + IF(eord.other = 0, (MID(eordrk.remarks__480, 7, 2) * 100), eord.other)) /
       100                                                                              AS total,
       CONCAT(paym.no, '-', paym.sname)                                                 AS MET,
       emp.empno                                                                        AS empno,
       LPAD(emp.empSname, 10, ' ')                                                      AS VENDEDOR,
       IF(custp.auxString2 = 'J', LPAD(custp.tel, 9, ' '), LPAD(custp.celular, 9, ' ')) AS WHATSAPP,
       ifnull(custp.name, '*')                                                          AS CLIENTE,
       custp.no                                                                         AS CODCLI,
       eord.amount / 100                                                                AS amount,
       emp.senha                                                                        AS senha
FROM sqldados.eord
  LEFT JOIN sqldados.eoprd
	      ON (eoprd.storeno = eord.storeno AND eoprd.ordno = eord.ordno)
  LEFT JOIN sqldados.prd
	      ON (prd.no = eoprd.prdno)
  LEFT JOIN sqldados.custp
	      ON (custp.no = eord.custno)
  LEFT JOIN sqldados.eordrk
	      ON (eordrk.storeno = eord.storeno AND eordrk.ordno = eord.ordno)
  LEFT JOIN sqldados.paym
	      ON (paym.no = eord.paymno)
  LEFT JOIN sqldados.T_EMP AS emp
	      ON (emp.empno = eord.empno)
WHERE (eord.storeno = :storeno OR :storeno = 0)
  AND paym.no IN (311, 312, 313)
  AND (eord.date >= :data)
GROUP BY eord.storeno, eord.ordno;

DROP TABLE IF EXISTS sqldados.TCARDBANCO01;
CREATE TEMPORARY TABLE sqldados.TCARDBANCO01 (
  PRIMARY KEY (storeno, pdvno, xano)
)
SELECT storeno,
       pdvno,
       xano,
       P.eordno         AS ordno,
       SUM(C.amt / 100) AS valor,
       C.cardno         AS banco,
       C.autorz         AS autorizacao
FROM sqlpdv.pxacrd      AS C
  INNER JOIN sqlpdv.pxa AS P
	       USING (storeno, pdvno, xano)
WHERE P.date >= :data
  AND P.storeno IN (2, 3, 4, 5, 6)
  AND (P.storeno = :storeno OR :storeno = 0)
  AND C.cardno IN (198, 199)
GROUP BY storeno, pdvno, xano;

DROP TABLE IF EXISTS sqldados.TCARDBANCO;
CREATE TEMPORARY TABLE sqldados.TCARDBANCO (
  PRIMARY KEY (storeno, ordno)
)
SELECT storeno,
       ordno,
       valor,
       banco,
       autorizacao
FROM sqldados.TCARDBANCO01
GROUP BY storeno, ordno;


SELECT P.storeno                                           AS loja,
       P.ordno                                             AS numPedido,
       IF(P.date = 0, NULL, cast(P.date AS DATE))          AS dataPedido,
       TPED.empno                                          AS empno,
       CAST(CONCAT(TPED.empno, ' ', TPED.VENDEDOR) AS CHAR) AS vendedor,
       IFNULL(senha, '')                                   AS senhaVendedor,
       P.paymno                                            AS metodo,
       frete                                               AS valorFrete,
       IF(frete IS NULL, TPED.amount, total)               AS valorPedido,
       DEPOSITANTE                                         AS depositante,
       CLIENTE                                             AS cliente,
       ifnull(cast(IFNULL(N.nfno, F.nfno) AS CHAR), '')    AS nfnoNota,
       IFNULL(IFNULL(N.nfse, F.nfse), '')                  AS nfseNota,
       if(IFNULL(N.issuedate, F.issuedate) = 0, NULL,
	  cast(IFNULL(N.issuedate, F.issuedate) AS DATE))  AS dataNota,
       IFNULL(N.grossamt, F.grossamt) / 100                AS valorNota,
       B.valor                                             AS valorTransf,
       B.banco                                             AS banco,
       B.autorizacao                                       AS autorizacao,
       P.status                                            AS status,
       P.c2                                                AS marca,
       P.s12                                               AS userTransf,
       P.m16 / 100                                         AS valorTransfEdt,
       P.c6                                                AS autorizacaoEdt
FROM sqldados.eord               AS P
  INNER JOIN sqldados.custp      AS C
	       ON C.no = P.custno
  LEFT JOIN  sqldados.nf         AS N
	       ON N.storeno = P.storeno AND N.nfno = P.nfno AND N.nfse = P.nfse
  LEFT JOIN  sqldados.nf2        AS N2
	       ON N.storeno = N2.storeno AND N.pdvno = N2.pdvno AND N.xano = N2.xano
  LEFT JOIN  sqldados.nf         AS F
	       ON F.storeno = P.storeno AND F.nfno = P.nfno_futura AND F.nfse = P.nfse_futura
  LEFT JOIN  sqldados.nf2        AS F2
	       ON F.storeno = F2.storeno AND F.pdvno = F2.pdvno AND F.xano = F2.xano
  INNER JOIN sqldados.users      AS U
	       ON U.no = P.userno
  INNER JOIN sqldados.TPED
	       ON TPED.storeno = P.storeno AND TPED.ordno = P.ordno
  LEFT JOIN  sqldados.TCARDBANCO AS B
	       ON B.storeno = P.storeno AND B.ordno = P.ordno
WHERE P.paymno IN (311, 312, 313)
  AND P.date >= :data
  AND P.status <> 5
  AND (P.storeno = :storeno OR :storeno = 0)
GROUP BY P.ordno, P.storeno

