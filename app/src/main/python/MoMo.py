from Crypto.PublicKey import RSA
from Crypto.Cipher import PKCS1_v1_5 as Cipher_PKCS1_v1_5
from base64 import b64decode, b64encode
import json
import urllib.request as urllib2
import uuid
import hmac
import hashlib

def hashRSA(partnerCode, partnerRefId, amount):
	# load key
	#pubKey = r"MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAh+fBkzLrYk7dMySeASH3U6Z8+sANdmYbESOzvoaMIJ4RsvE94HJY8mw6fcoq5NddSIcdJa2RabDCBaYvkljiW5064wFW9xRhUhT9lM/JI8w9WG01499qfn1+m1hAOx8CNGJuz91r/kQL7S+xCTc+s+mO0EwLWRqUiFVQZtPAXdR/wg1UTHuY1zOmoD0dWq5yO562tO2fOLKIlYAe5zC0+J4yypGODLN0FJ5OdGH99WNCHpYzJE1PgIOOKnadS04ql0wywfZtQTSp8mex/qAazwVZYNPLO5NtwV5ReSXDCNei+w0zdi3cui7l7KWbWuy9luwru32cKG5N6R9KKU+j5/X3g86HviCmasPx/azQXyyUl2MmAumjRRDkPv+iBIUjvc6+L7iUJNmOeclpOySSKP43K6oFyx+8KbCJNu2Y0xR2xCYrfWIqGsahO5HIm0VzER2k1kxxxlMvcjt9Yu6eqm1YykA1/zUc9zcaYG/EL3sypHdc1yJp6jBNC2wTWkcE+MpmGAH8B+uKOw8hNvoV1i5p18AFBPKxBNdus01cxKdg5lkRddqAtj2vMuR/UYUJhmB76wjc0hVSp/AvZP9nh2bwT7IMzzaOmzH98QvLyppJw4hTT0F2rJpdNqY4KG4y7EKBbc4W1CQGSDksgORRhsQ/26GMaITAXu26gvCkkN8CAwEAAQ=="
	#keyDER = b64decode(pubKey)
	#key = RSA.importKey(keyDER)
	f = open('mykey.pem', 'r')
	key = RSA.importKey(f.read())

	# encrypt data
	
	rowData = {
	  'partnerCode': partnerCode,
	  'partnerRefId': partnerRefId,
	  'partnerTransId': '8374736463',
	  'amount': amount,
	}	

	cipher = Cipher_PKCS1_v1_5.new(key)
	cipher_text = cipher.encrypt(json.dumps(rowData).encode())
	emsg = b64encode(cipher_text)
	return emsg

def httpRequest(partnerCode, partnerRefId, customerNumber, appData, amount):
	endpoint = "https://test-payment.momo.vn/pay/app"
	hash = hashRSA(partnerCode, partnerRefId, amount)
	hash = hash.decode("utf-8")
	hash = r"A7WFmmnpn6TRX42Akh/iC5DdU5hhBT9LR5QSG6rJAl70hfEkkGUx2pTCai8s+M9KMVUcJ7m52iv74yhmeEjjN10TtEJoqITBIYBG2bqcTprhDijyhV4ePU7ytDNuLxzzIvGfTYyvbsEJ2jZTSf556yod12vhYqOJSFL/U2hVuxjUahf5Rnu5R/OLalg8QmlU6nQooEuNdzEXPMd6j9EaxOCiB2oM5/9QiTN0tCNSTIVvPtnlHu5mIbBHChcwfToIL4IAiD1nbrlDuBX//CZcrZj6hFqjvU31yb/DuG02c3aqWxbZKZ8csOwF9bL30m/yGr/0BQUWgunpDPrmCosf9A=="
	print(hash)
	
	data = {
			'customerNumber' : customerNumber,
		    'partnerCode' : partnerCode,
		  	'partnerRefId' : partnerRefId,
			'appData' : appData,
			'hash' : hash,
			'version' : 2,
			'description': 'Thanh toan cho don hang Merchant123556666qua MoMo'
			}
	print("--------------------JSON REQUEST----------------\n")
	"""
	data = json.dumps({
			'partnerCode' : "MOMOIQA420180417",
		  	'partnerRefId' : "Merchant123556666",
		  	'customerNumber' : "0966787273",
			'appData' : r"v2/Wr0gaBVBnBVtVEDQ3elzaq/JkDaz12x9FKZcgdHwc2GuDtXQaWRw2geweTBOCCU8Av1ehECUAvbLHDcoPxM7PjZ+JNUarjU410olhwG9ryK+nOzMnzOhsWYlYkBT3QSdpgeWhtz+woN1Kp3dpUa5Wg/MyJIAax/M2nGdb6vLQ1+7gJ81nZxaVLIO3VrVAVaOqa4IzFnolB6ratfsTkcHlwrBf8OFT5VKhfcSGgUPGKtq0tuoO6paI/7lWvgRpuCgWs7yMYAKrDY7CDqRlCHGnnbn2Js4r46aUP5r2LwX/M8eH63lRGKF7bbb8V/+pXVgdnyWGVoifXYgUIoVGxOC1nsvcjO9MCWrQhB59NdrY8jyEXLwCF5sO1ZPInDouPXRJ6pjWOwQvI0cFV/nS1Rgs+v25efSc7enbkTk9q5nM/rlsQjXmtN46gbE5vIStGYaSFyrRLFoU4K4PXIdkf0dBIE1GgFlSrBZYJoTAmrPFChdWsM0O60rDWnXbCaBtdL/+rHEvKP2gtgqgm7lBOogaBXV8lD5sEb5z8OJCNme1PhfbgACh1vwU5LmtDKEx0HBY60Em0UUrZWmmHpbTigIbdst9eey7+9wT77nT3DNqG+9c7V+68hQW3ZSeIeDbCDEmf8LSFdw7qTepVMhYF+pfDHgpeMYsFNeaKIKCbV6rHr46uQacGa+XriJdApImlx2ULjv9cCKGqzuswLUMeWlDmeURk7jB3QJNUqzS06K2Yek/L0pcjqrJrutOyBnCq7V7BjXhDCXnEQrZd+F4+s2HQTDuqk+VD3haOzb/PvhT3Q+mqQTAFLdUKcUZWz2F+Be9/1IypkGam07RdHZ6vqP/5/n3bnGQ4pGEoNkCVhr3zVEIVDevyqY1oIFvkxs/rb5JywbxW8FhWwIkjBYArRmn7v8JpvE5xlSQJZ7EwjqrXdocPfKLtWHRhe1ivbdeou0Tkuj1HRxqIs/O9OsbbpnEBgJ5XbF2vdSHEgQyKSqraVB7gli3UKY883DbaRnlv0QbSaBTrxoW4s29GbhYNknegP6l639Cpm4jP6HSC7inwbInZcrVJbs0DdkPTaNouUi8HJ69QYcAJ98IrTm/1vKJB3ERPxwe/90be1jzZjkmfeU5QBVjaQCMK/HUVuQR9UvUSxOTf0SYAuKohTGIVhfdil7Uq27EsilocXM9anwaigmI7wwUPJTsI+rICOYKhHx",
			'hash' : r"A7WFmmnpn6TRX42Akh/iC5DdU5hhBT9LR5QSG6rJAl70hfEkkGUx2pTCai8s+M9KMVUcJ7m52iv74yhmeEjjN10TtEJoqITBIYBG2bqcTprhDijyhV4ePU7ytDNuLxzzIvGfTYyvbsEJ2jZTSf556yod12vhYqOJSFL/U2hVuxjUahf5Rnu5R/OLalg8QmlU6nQooEuNdzEXPMd6j9EaxOCiB2oM5/9QiTN0tCNSTIVvPtnlHu5mIbBHChcwfToIL4IAiD1nbrlDuBX//CZcrZj6hFqjvU31yb/DuG02c3aqWxbZKZ8csOwF9bL30m/yGr/0BQUWgunpDPrmCosf9A==",
			'version' : 2.0,
		  	"description": "Thanh toan cho don hang Merchant123556666qua MoMo",
			})
	"""
	data = json.dumps(data)
	print(data)
	clen = len(data)
	data = data.encode('ascii')
	req = urllib2.Request(endpoint, data, {'Content-Type': 'application/json', 'Content-Length': clen})
	f = urllib2.urlopen(req)

	response = f.read()
	f.close()
	print("--------------------JSON response----------------\n")
	print(response)
	print(json.loads(response))
	

"""
def mm():
	#parameters send to MoMo get get payUrl
	endpoint = "https://test-payment.momo.vn/gw_payment/transactionProcessor"
	partnerCode = "MOMO"
	accessKey = "F8BBA842ECF85"
	serectkey = "K951B6PE1waDMi640xX08PD3vg6EkVlz"
	orderInfo = "pay with MoMo"
	returnUrl = "https://momo.vn/return"
	notifyurl = "https://dummy.url/notify"
	amount = "50000"
	orderId = str(uuid.uuid4())
	requestId = str(uuid.uuid4())
	requestType = "captureMoMoWallet"
	extraData = "merchantName=;merchantId=" #pass empty value if your merchant does not have stores else merchantName=[storeName]; 		merchantId=[storeId] to identify a transaction map with a physical store

	#before sign HMAC SHA256 with format
	#partnerCode=$partnerCode&accessKey=$accessKey&requestId=$requestId&amount=$amount&orderId=$oderId&orderInfo=$orderInfo&returnUrl=$returnUrl&notifyUrl=$notifyUrl&extraData=$extraData
	rawSignature = "partnerCode="+partnerCode+"&accessKey="+accessKey+"&requestId="+requestId+"&amount="+amount+"&orderId="+orderId+"&orderInfo="+orderInfo+"&returnUrl="+returnUrl+"&notifyUrl="+notifyurl+"&extraData="+extraData


	#puts raw signature
	print "--------------------RAW SIGNATURE----------------"
	print rawSignature
	#signature
	h = hmac.new( serectkey, rawSignature, hashlib.sha256 )
	signature = h.hexdigest()
	print "--------------------SIGNATURE----------------"
	print signature

	#json object send to MoMo endpoint

	data = {
		    'partnerCode' : partnerCode,
		  	'accessKey' : accessKey,
		  	'requestId' : requestId,
		  	'amount' : amount,
		  	'orderId' : orderId,
		  	'orderInfo' : orderInfo,
		  	'returnUrl' : returnUrl,
		  	'notifyUrl' : notifyurl,
		  	'extraData' : extraData,
		  	'requestType' : requestType,
		  	'signature' : signature
	}
	print "--------------------JSON REQUEST----------------\n"
	data = json.dumps(data)
	print data

	clen = len(data)
	req = urllib2.Request(endpoint, data, {'Content-Type': 'application/json', 'Content-Length': clen})
	f = urllib2.urlopen(req)

	response = f.read()
	f.close()
	print "--------------------JSON response----------------\n"
	print response+"\n"

	print "payUrl\n"
	print json.loads(response)['payUrl']+"\n"
"""

if __name__ == "__main__":
	
	partnerCode = "MOMOIQA420180417"
	partnerRefId = "Merchant123556666"
	customerNumber = "0919100010"
	appData = r"v2/M6VtslOXbL9s6sFGs2ZKInmRpmE2btaQIfHVmvA8JjcfD3LiH4FQvT/Udynjn+ei97YR1UXFgyLtEqYEDtnZtuTVRXjfRTwECIO4nYWFlSgsdZ3irz1D7Iwm6H+DoN20Psm1Tiik+ieJDaHiH9eJnJRS4Uaz7pinO3wxJM1MX3Xq3cnjf/M+3UnGAh5hMmz5cdKLfKffzaV2iuq4GJOTLOxBBlKErZfvmqoNC0DWPyi6K0M1pO3j/5Slc8DZ+qQXUoF5tpijq3ML2oCIcknimfaV8g3ISaurWZVSwtDp/oSykSHkI6rdeHXxE8O/qPQG59Q70qcIKMJF5HHtQ9JrVEJBtS7h/ISCgOcHvfEu+YetvshJj572ucDXJo/xaKqj+VlqfhMe8UlHRn6TJT6lr27EgRM35DvzbCQfMo4KtGIZhtG8xZg3vCnTeYxflU3NSRcTIdPgpMzbrVrwPNCdiZ6foUbRbP2MFLYohKDBRLjmCwuJAbJKgTdJnzpmuTS+C3PvUyw9w99NkVALwsqbui6Jl52f1a995UiIh26+nOexUhQ3YcMJ30OzNHjANUEdMo3SB332emGAPPmmiNcZpopTriDuD6CVM2Fpq6+Rhx6gY93FkoC9sP0UPvPVIWH7769cxUppQsiU+IKMdQH6laBgnGlKD8KANGJSmOqKjnmCd7xjuUArpcVA5FLaUsFavTAYtUYVXSRy84eeTlli4Cn3x4RysRrEac8AxdQDrd2Au+CK54d8TtgkrOm5Rug2tNsdxqcUwv0o7CFFz8zA0Vlxaz8Wq2Mq+/5se3DolCqizx1miu7HVP4dQ+V3yNuL2no0oB44doy9zElvFunvUqhQEJZ4ydjfLQUXrmb/1ZckRnp51zTJXW1B9v3nnnInz0lQ067qVe2Cw+9ZTR6OL4i2qezYJf8MNbf4wt9lgg4r3dIHhHzgi9f/MMegGg7r2fKM6q+eCWWUbqwJV/is7DjM+d24xPGqbEWRgynIWOJ7tcN5RHCnElOIGsVa9mLA4qjOnQRn6rA1leAhWXlaiC70s4W6YSccGe073YzNo7zQv8twVAphZHgWCYOWsym1y1hEve1k4eRRQyckQD9ue87aEn3IHj5Be+kDx1mdsTkQvqRVI2B901Ytjpc+nriaazjkFEiEMvqmXecBB74arjcLtAAacwuK42+6rHMP24A="
	amount = "10000"
	httpRequest(partnerCode, partnerRefId, customerNumber, appData, amount)

