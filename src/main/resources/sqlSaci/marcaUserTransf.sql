UPDATE sqldados.eord
SET eord.s12 = :userLink
WHERE ordno = :ordno
  AND storeno = :storeno