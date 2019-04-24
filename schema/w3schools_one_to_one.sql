CREATE TABLE Customers_Extra (
    customer_ID int(11) NOT NULL,
    last_message varchar(512),
    mobile_phone varchar(9),
    PRIMARY KEY (customer_ID),
    FOREIGN KEY (customer_ID) REFERENCES customers(CustomerID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO CUSTOMERS_EXTRA(
    customer_id,
    last_message,
    mobile_phone
)
SELECT
    customerID,
    ELT(
        0.5 + RAND() * 6, 'Mensaje Aleatorio', 'Me ha gustado comprar aquí', 'No me ha llegado el producto aún', '¿Dónde está mi recibo?', 'Me he equivocado de pedido', 'Quiero la hoja de reclamaciones...'),
        CONCAT('6', FLOOR(RAND() * 99999999))
    FROM
        customers;