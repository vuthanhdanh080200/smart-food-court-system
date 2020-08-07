from Crypto.PublicKey import RSA
from Crypto.Cipher import PKCS1_v1_5 as Cipher_PKCS1_v1_5
from base64 import b64decode, b64encode
import json

def hash(partnerCode, partnerRefId, amount):
	# load key
	pubKey = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAh+fBkzLrYk7dMySeASH3U6Z8+sANdmYbESOzvoaMIJ4RsvE94HJY8mw6fcoq5NddSIcdJa2RabDCBaYvkljiW5064wFW9xRhUhT9lM/JI8w9WG01499qfn1+m1hAOx8CNGJuz91r/kQL7S+xCTc+s+mO0EwLWRqUiFVQZtPAXdR/wg1UTHuY1zOmoD0dWq5yO562tO2fOLKIlYAe5zC0+J4yypGODLN0FJ5OdGH99WNCHpYzJE1PgIOOKnadS04ql0wywfZtQTSp8mex/qAazwVZYNPLO5NtwV5ReSXDCNei+w0zdi3cui7l7KWbWuy9luwru32cKG5N6R9KKU+j5/X3g86HviCmasPx/azQXyyUl2MmAumjRRDkPv+iBIUjvc6+L7iUJNmOeclpOySSKP43K6oFyx+8KbCJNu2Y0xR2xCYrfWIqGsahO5HIm0VzER2k1kxxxlMvcjt9Yu6eqm1YykA1/zUc9zcaYG/EL3sypHdc1yJp6jBNC2wTWkcE+MpmGAH8B+uKOw8hNvoV1i5p18AFBPKxBNdus01cxKdg5lkRddqAtj2vMuR/UYUJhmB76wjc0hVSp/AvZP9nh2bwT7IMzzaOmzH98QvLyppJw4hTT0F2rJpdNqY4KG4y7EKBbc4W1CQGSDksgORRhsQ/26GMaITAXu26gvCkkN8CAwEAAQ=="
	keyDER = b64decode(pubKey)
	key = RSA.importKey(keyDER)
	

	# encrypt data
	rowData = {
	  'partnerCode': partnerCode,
	  'partnerRefId': partnerRefId,
	  'amount': amount,
	  'partnerTransId': 'c1f10470-8a68-11e8-9a57'
	}
	cipher = Cipher_PKCS1_v1_5.new(key)
	cipher_text = cipher.encrypt(json.dumps(rowData).encode())
	emsg = b64encode(cipher_text)
	return emsg

"""
if __name__ == "__main__":
	partnerCode = "MOMOO1KC20200802"
	partnerRefId = "orderId123456789"
	amount = "10000"
	s = hash(partnerCode, partnerRefId, amount)
	print(s)"""

