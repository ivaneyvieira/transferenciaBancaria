SET SQL_MODE = '';

USE sqldados;

DROP TABLE IF EXISTS sqldados.T_EMP;
CREATE TEMPORARY TABLE sqldados.T_EMP
(
  PRIMARY KEY (empno)
)
SELECT no                                                            AS empno,
       name                                                          AS empName,
       sname                                                         AS empSname,
       TRIM(CAST(CONCAT(CHAR(ASCII(MID(pswd, 1, 1)) - 5), CHAR(ASCII(MID(pswd, 2, 1)) - 7),
                        CHAR(ASCII(MID(pswd, 3, 1)) - 8), CHAR(ASCII(MID(pswd, 4, 1)) - 0),
                        CHAR(ASCII(MID(pswd, 5, 1)) - 34), CHAR(ASCII(MID(pswd, 6, 1)) - 9),
                        CHAR(ASCII(MID(pswd, 7, 1)) - 9),
                        CHAR(ASCII(MID(pswd, 8, 1)) - 13)) AS CHAR)) AS senha
FROM sqldados.emp
WHERE pswd <> '';

DROP TEMPORARY TABLE IF EXISTS sqldados.TPED;
CREATE TEMPORARY TABLE sqldados.TPED
(
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
       IFNULL(custp.name, '*')                                                          AS CLIENTE,
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
CREATE TEMPORARY TABLE sqldados.TCARDBANCO01
(
  PRIMARY KEY (storeno, pdvno, xano)
)
SELECT storeno,
       pdvno,
       xano,
       P.eordno         AS ordno,
       SUM(C.amt / 100) AS valor,
       C.cardno         AS banco,
       C.autorz         AS autorizacao
FROM sqlpdv.pxacrd AS C
       INNER JOIN sqlpdv.pxa AS P
                  USING (storeno, pdvno, xano)
WHERE P.date >= :data
  AND P.storeno IN (2, 3, 4, 5, 6)
  AND (P.storeno = :storeno OR :storeno = 0)
  AND C.cardno IN (198, 199)
GROUP BY storeno, pdvno, xano;

DROP TABLE IF EXISTS sqldados.TCARDBANCO;
CREATE TEMPORARY TABLE sqldados.TCARDBANCO
(
  PRIMARY KEY (storeno, ordno)
)
SELECT storeno,
       ordno,
       valor,
       banco,
       autorizacao
FROM sqldados.TCARDBANCO01
GROUP BY storeno, ordno;


DROP TEMPORARY TABLE IF EXISTS T_TRANSF;
CREATE TEMPORARY TABLE T_TRANSF
(
  PRIMARY KEY (loja, numPedido)
)
SELECT P.storeno                                            AS loja,
       P.ordno                                              AS numPedido,
       IF(P.date = 0, NULL, CAST(P.date AS DATE))           AS dataPedido,
       TPED.empno                                           AS empno,
       CAST(CONCAT(TPED.empno, ' ', TPED.VENDEDOR) AS CHAR) AS vendedor,
       IFNULL(senha, '')                                    AS senhaVendedor,
       P.paymno                                             AS metodo,
       TPED.frete                                           AS valorFrete,
       IF(frete IS NULL, TPED.amount, total)                AS valorPedido,
       DEPOSITANTE                                          AS depositante,
       CLIENTE                                              AS cliente,
       IFNULL(CAST(IFNULL(N.nfno, F.nfno) AS CHAR), '')     AS nfnoNota,
       IFNULL(IFNULL(N.nfse, F.nfse), '')                   AS nfseNota,
       IF(IFNULL(N.issuedate, F.issuedate) = 0, NULL,
          CAST(IFNULL(N.issuedate, F.issuedate) AS DATE))   AS dataNota,
       IFNULL(N.grossamt, F.grossamt) / 100                 AS valorNota,
       B.valor                                              AS valorTransf,
       B.banco                                              AS banco,
       B.autorizacao                                        AS autorizacao,
       P.status                                             AS status,
       P.c2                                                 AS marca,
       P.s12                                                AS userTransf,
       P.m16 / 100                                          AS valorTransfEdt,
       P.c6                                                 AS autorizacaoEdt
FROM sqldados.eord AS P
       INNER JOIN sqldados.custp AS C
                  ON C.no = P.custno
       LEFT JOIN sqldados.nf AS N
                 ON N.storeno = P.storeno AND N.nfno = P.nfno AND N.nfse = P.nfse
       LEFT JOIN sqldados.nf AS F
                 ON F.storeno = P.storeno AND F.nfno = P.nfno_futura AND F.nfse = P.nfse_futura
       INNER JOIN sqldados.users AS U
                  ON U.no = P.userno
       INNER JOIN sqldados.TPED
                  ON TPED.storeno = P.storeno AND TPED.ordno = P.ordno
       LEFT JOIN sqldados.TCARDBANCO AS B
                 ON B.storeno = P.storeno AND B.ordno = P.ordno
WHERE P.paymno IN (311, 312, 313)
  AND P.date >= :data
  AND P.status <> 5
  AND (P.storeno = :storeno OR :storeno = 0)
GROUP BY P.ordno, P.storeno;

DROP TEMPORARY TABLE IF EXISTS T_FAT;
CREATE TEMPORARY TABLE T_FAT
(
  PRIMARY KEY (loja, numPedido)
)
SELECT storeno AS loja, eordno AS numPedido, date, nfno, nfse, cfo
FROM sqlpdv.pxa AS P
       INNER JOIN T_TRANSF AS T
                  ON P.storeno = T.loja
                    AND P.eordno = T.numPedido
WHERE nfse = '1'
GROUP BY storeno, eordno;

DROP TEMPORARY TABLE IF EXISTS T_ENT;
CREATE TEMPORARY TABLE T_ENT
(
  PRIMARY KEY (loja, numPedido)
)
SELECT storeno AS loja, eordno AS numPedido, date, nfno, nfse, cfo
FROM sqlpdv.pxa AS P
       INNER JOIN T_TRANSF AS T
                  ON P.storeno = T.loja
                    AND P.eordno = T.numPedido
WHERE nfse = '3'
GROUP BY storeno, eordno;

SELECT T.loja,
       T.numPedido,
       T.dataPedido,
       T.empno,
       T.vendedor,
       T.senhaVendedor,
       T.metodo,
       T.valorFrete,
       T.valorPedido,
       T.depositante,
       T.cliente,
       T.nfnoNota,
       T.nfseNota,
       T.dataNota,
       T.valorNota,
       T.valorTransf,
       T.banco,
       T.autorizacao,
       T.status,
       T.marca,
       T.userTransf,
       T.valorTransfEdt,
       T.autorizacaoEdt,
       CAST(F.date AS DATE) AS dataFat,
       CAST(F.nfno AS CHAR) AS nfnoFat,
       F.nfse               AS nfseFat,
       F.cfo                AS cfoFat
FROM T_TRANSF AS T
       LEFT JOIN T_FAT AS F
                 USING (loja, numPedido)

