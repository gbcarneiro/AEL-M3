import './Filter.css'
import { IoIosArrowBack } from "react-icons/io";
import { useState } from 'react';

interface FilterProps {
  show: boolean;
  toggleFilter: () => void;
}

function Filter(props: FilterProps) {
  const { show, toggleFilter } = props;
  
  const [clickedButtons, setClickedButtons] = useState<number[]>([])

  const handleClick = (index: number) => {
    if (clickedButtons.includes(index)) setClickedButtons(clickedButtons.filter(i => i !== index))
    else setClickedButtons([...clickedButtons, index])
  }

  return (
    <div className={`container ${show ? 'show' : ''}`}>
      <div className={`menu ${show ? 'show' : ''}`}>
        <div className="filter-header">
          <p>Filtro</p>
          <button id='close-button' onClick={toggleFilter} style={{"cursor" : "pointer"}} ><IoIosArrowBack /></button>
        </div>
        <hr/>
        <div className="filter-buttons">
          <button id='1' className={`filter-button ${clickedButtons.includes(1)  ? "clicked" : ""}`} onClick={() => handleClick(1)}>Mostrar zonas de exclusão</button>
          <button id='2' className={`filter-button ${clickedButtons.includes(2)  ? "clicked" : ""}`} onClick={() => handleClick(2)}>Altitude nos nós</button>
          <button id='3' className={`filter-button ${clickedButtons.includes(3)  ? "clicked" : ""}`} onClick={() => handleClick(3)}>Mudar tipo de visualização do mapa</button>
          <button id='4' className={`filter-button ${clickedButtons.includes(4)  ? "clicked" : ""}`} onClick={() => handleClick(4)}>Ativar variação de altitude nas arestas</button>
        </div>
      </div>
      <div className="background"></div>
    </div>
  )
}

export default Filter