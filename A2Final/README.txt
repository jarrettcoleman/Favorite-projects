Jarrett Coleman jjc368
William Li xl289
Known issues: we use the BigInteger GCD Function when checking that the public key e is relatively prime to the totient. We have no idea how this works and are unsure that it is an effective way of checking that e is relatively prime to the totient, but we decided to trust the function.
