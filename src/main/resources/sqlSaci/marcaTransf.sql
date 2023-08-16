UPDATE sqldados.eord
SET eord.m16 = ROUND(:valorTransfEdt * 100),
    eord.c6  = :autorizacao,
    eord.l12 = :dataTransf
WHERE ordno = :ordno
  AND storeno = :storeno